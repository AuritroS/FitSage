from pymongo import MongoClient
from dotenv import load_dotenv
import os
import certifi

load_dotenv()

_client = None
_db = None

def get_db():
    global _client, _db
    if not _client:
        uri = os.getenv("MONGO_URI")
        _client = MongoClient(uri, tlsCAFile=certifi.where())
        _db = _client["fitsage"]
    return _db

def get_users_collection():
    return get_db()["users"]

def get_workouts_collection():
    return get_db()["workouts"]
