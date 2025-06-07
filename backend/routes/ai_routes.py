from __future__ import annotations
import json, re, time, uuid, math
import requests
from flask import Blueprint, request, jsonify
from bson.objectid import ObjectId
from models.database import get_users_collection, get_workouts_collection

# ── Flask / DB handles ────────────────────────────────────────────────
ai_routes = Blueprint("ai_routes", __name__)
users_collection = get_users_collection()
workouts_collection = get_workouts_collection()

# ── Ollama config ─────────────────────────────────────────────────────
OLLAMA_URL = "http://localhost:11434/api/chat"
OLLAMA_MODEL = "llama2:7b"


def call_ollama_chat(messages, *, temperature=0.9, num_predict=4096) -> str:
    """Wrapper around Ollama /api/chat with extra logging."""
    payload = {
        "model":    OLLAMA_MODEL,
        "messages": messages,
        "stream":   False,
        "options": {
            "temperature":    temperature,
            "num_predict":    num_predict,
            "repeat_penalty": 1.1,
        },
    }
    r = requests.post(OLLAMA_URL, json=payload, timeout=120)
    r.raise_for_status()
    content = r.json()["message"]["content"].strip()
    return content


# ══════════════════════════════ ROUTES ════════════════════════════════
@ai_routes.route("/generate_workout/<user_id>", methods=["POST"])
def generate_workout(user_id: str):
    user = users_collection.find_one({"_id": ObjectId(user_id)})
    if not user or "profile" not in user:
        return jsonify({"error": "User profile not found"}), 404
    profile = user["profile"]

    body       = request.get_json() or {}
    duration   = int(body.get("duration", 30))
    focus      = body.get("focus", "").strip()
    focus_txt  = f" focusing on {focus}" if focus else ""

    # ---- target exercise count (≥2) ----------------------------------
    target_ex = max(2, math.ceil(duration / 5))

    uid = f"{time.time()}-{uuid.uuid4().hex}"

    # ---- system & user prompts ---------------------------------------
    system_msg = (
        f"(RequestID: {uid})\n"
        "You are a certified strength-and-conditioning coach. "
        f"Create a {duration}-minute workout with **exactly "
        f"{target_ex} exercises** – no more, no fewer. "
        "Scale sets / reps / timed holds so the whole session fits the time. "
        "Leverage the user’s goal, fitness level and equipment"
        f"{focus_txt}. Each answer must be fresh.\n\n"
        "Respond **only** with valid JSON:\n"
        "{ \"workout\": [ {\"name\": \"…\", \"sets\": 3, \"reps\": 12}, … ] }\n"
        "Each exercise: either (sets & reps) *or* (duration). "
        "No comments, no extra keys."
    )
    user_msg = (
        f"(RequestID: {uid})\n"
        "User profile ⇒\n"
        f"• Goal: {profile.get('goal')}\n"
        f"• Fitness level: {profile.get('fitness_level')}\n"
        f"• Equipment: {', '.join(profile.get('equipment', []))}\n\n"
        "Generate the workout JSON now."
    )

    messages = [
        {"role": "system", "content": system_msg},
        {"role": "user",   "content": user_msg},
    ]

    # ---- helpers -----------------------------------------------------
    def extract_json(text: str) -> str | None:
        m = re.search(r"\{[\s\S]*\}", text)
        return m.group(0) if m else None

    def try_parse(txt: str) -> dict | None:
        try:
            return json.loads(txt)
        except Exception:
            return None

    # ---- 1st attempt --------------------------------------------------
    raw1      = call_ollama_chat(messages)
    json_txt  = extract_json(raw1) or "{}"
    data      = try_parse(json_txt)

    # ---- retry if count wrong / json bad ------------------------------
    if (
        not data
        or "workout" not in data
        or not isinstance(data["workout"], list)
        or len(data["workout"]) != target_ex
    ):
        messages.append({
            "role": "system",
            "content": (
                "❗ Return **JSON only** (no apologies, no prose) with "
                f"exactly {target_ex} exercises. Begin with '{{', end with '}}'."
            )
        })
        raw2     = call_ollama_chat(messages, temperature=0.3)
        json_txt = extract_json(raw2) or "{}"
        data     = try_parse(json_txt)

    # ---- final sanity / salvage --------------------------------------
    if not data or "workout" not in data or not isinstance(data["workout"], list):
        # salvage: trim first attempt if something usable exists
        fallback = (try_parse(extract_json(raw1) or "{}") or {}).get("workout", [])
        fallback = fallback[:target_ex]
        if not fallback:
            return jsonify({"error": "Unable to generate workout."}), 500
        data = {"workout": fallback}

    # ensure exactly target_ex items
    data["workout"] = (data["workout"] + [])[:target_ex]

    return jsonify(data)


# ────────────────────── chat route ────────────────────────────
@ai_routes.route("/chat/<user_id>", methods=["POST"])
def chat(user_id):
    user = users_collection.find_one({"_id": ObjectId(user_id)})
    if not user or "profile" not in user:
        return jsonify({"error": "User profile not found"}), 404
    profile = user["profile"]

    data = request.get_json() or {}
    user_message = data.get("message", "").strip()
    previous_messages = data.get("history", [])

    if not user_message:
        return jsonify({"error": "No message provided"}), 400

    unique_id = f"{time.time()}-{uuid.uuid4().hex}"

    # Always build a single “profile” block.
    profile_block = (
        f"User profile:\n"
        f"- Goal: {profile.get('goal')}\n"
        f"- Fitness Level: {profile.get('fitness_level')}\n"
        f"- Equipment: {', '.join(profile.get('equipment', []))}"
    )

    # Combine profile + concise instructions into one system prompt:
    system_msg = (
        f"(RequestID: {unique_id})\n"
        "You are a concise fitness coach. "
        f"{profile_block}\n\n"
        "Continue the conversation naturally, tailor responses based on the user profile, stay context‐aware, "
        "and keep answers short (2–4 sentences)."
    )

    # Build the full “messages” array:
    #   1) New system prompt (with profile always included)
    #   2) Any prior turns (role/content) from the client
    #   3) The current user message
    messages = [
        {"role": "system", "content": system_msg}
    ] + previous_messages + [
        {"role": "user", "content": user_message}
    ]

    try:
        reply = call_ollama_chat(messages, temperature=0.7)
        return jsonify({"reply": reply})

    except Exception as e:
        return jsonify({"error": f"AI chat failed: {str(e)}"}), 500


# ──────────────── history routes ────────────────
@ai_routes.route("/log_workout/<user_id>", methods=["POST"])
def log_workout(user_id: str):
    body = request.get_json() or {}
    res = workouts_collection.insert_one({
        "user_id":   ObjectId(user_id),
        "date":      body.get("date"),
        "exercises": body.get("exercises", []),
        "feedback":  body.get("feedback", ""),
    })
    return jsonify({"message": "Workout logged",
                    "workout_id": str(res.inserted_id)}), 201


@ai_routes.route("/workouts/<user_id>", methods=["GET"])
def get_workout_history(user_id: str):
    docs = list(workouts_collection.find({"user_id": ObjectId(user_id)}))
    for d in docs:
        d["_id"] = str(d["_id"])
        d["user_id"] = str(d["user_id"])
    return jsonify({"workouts": docs})
