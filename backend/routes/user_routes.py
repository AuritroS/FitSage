from flask import Blueprint, request, jsonify
from bson.objectid import ObjectId
from werkzeug.security import generate_password_hash, check_password_hash
from models.database import get_users_collection

user_routes = Blueprint("user_routes", __name__)
users_collection = get_users_collection()

# Test DB Connection
@user_routes.route("/test-db", methods=["GET"])
def test_db_connection():
    try:
        user = users_collection.find_one()
        if user:
            return jsonify({"status": "success", "message": "Connected to MongoDB", "sample_user": str(user["_id"])})
        else:
            return jsonify({"status": "success", "message": "Connected to MongoDB, but no users found"})
    except Exception as e:
        return jsonify({"status": "error", "message": f"Database connection failed: {str(e)}"}), 500


# Signup
@user_routes.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()
    email = data.get("email")
    username = data.get("username")
    password = data.get("password")

    if not email or not username or not password:
        return jsonify({"error": "All fields are required"}), 400

    if users_collection.find_one({"email": email}):
        return jsonify({"error": "Email already exists"}), 409

    hashed_pw = generate_password_hash(password)

    new_user = {
        "email": email,
        "username": username,
        "password": hashed_pw,
        "profile": {},
        "history": []
    }

    result = users_collection.insert_one(new_user)
    return jsonify({"message": "User created", "user_id": str(result.inserted_id)}), 201


# Login
@user_routes.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    email = data.get("email")
    password = data.get("password")

    user = users_collection.find_one({"email": email})
    if not user or not check_password_hash(user["password"], password):
        return jsonify({"error": "Invalid email or password"}), 401

    return jsonify({
        "message": "Login successful",
        "user_id": str(user["_id"]),
        "username": user["username"]
    })


# Save or Update Profile
@user_routes.route("/profile/<user_id>", methods=["POST"])
def update_profile(user_id):
    data = request.get_json()
    profile = {
        "fitness_level": data.get("fitness_level"),
        "goal": data.get("goal"),
        "equipment": data.get("equipment", [])
    }

    result = users_collection.update_one(
        {"_id": ObjectId(user_id)},
        {"$set": {"profile": profile}}
    )

    if result.modified_count:
        return jsonify({"message": "Profile updated"}), 200
    else:
        return jsonify({"message": "No changes made"}), 200


# Get Profile
@user_routes.route("/profile/<user_id>", methods=["GET"])
def get_profile(user_id):
    user = users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        return jsonify({"error": "User not found"}), 404

    return jsonify({
        "username": user["username"],
        "email": user["email"],
        "profile": user.get("profile", {})
    })
