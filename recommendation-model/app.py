from flask import Flask, request, jsonify
from flask_cors import CORS
from src.recommend import recommend

app = Flask(__name__)
CORS(app)

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ML service is running"})

@app.route("/recommend", methods=["POST"])
def get_recommendations():
    data = request.get_json()

    user_id = data.get("user_id")
    top_n = data.get("top_n", 3)

    if not user_id:
        return jsonify({"error": "user_id is required"}), 400

    recommendations = recommend(user_id, top_n)

    return jsonify({
        "user_id": user_id,
        "recommendations": recommendations.tolist()
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)