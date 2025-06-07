from flask import Flask
from routes.user_routes import user_routes
from routes.ai_routes import ai_routes  

app = Flask(__name__)
app.config["JSON_SORT_KEYS"] = False

# Register routes
app.register_blueprint(user_routes)
app.register_blueprint(ai_routes)

if __name__ == "__main__":
    app.run(debug=True, port=5001)
