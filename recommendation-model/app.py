import os
import logging

from flask import Flask, request, jsonify
from flask_cors import CORS
from src.recommend import recommend

# ---------------------------------------------------------------------------
# Application Setup
# ---------------------------------------------------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s — %(message)s",
)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)


# ---------------------------------------------------------------------------
# Endpoints
# ---------------------------------------------------------------------------

@app.route("/health", methods=["GET"])
def health():
    """Health check endpoint for Docker and monitoring."""
    return jsonify({"status": "ML service is running"})


@app.route("/recommend", methods=["POST"])
def get_recommendations():
    """
    Get trip recommendations for a user.

    Request JSON:
        {
            "user_id": 1,
            "top_n": 3        (optional, default 3)
        }

    Success Response (200):
        {
            "user_id": 1,
            "recommendations": [3, 1, 6]
        }

    Error Responses:
        400 — missing user_id
        404 — user_id not found in training data
        500 — unexpected server error
    """
    data = request.get_json()

    if not data or "user_id" not in data:
        return jsonify({"error": "user_id is required"}), 400

    user_id = data["user_id"]
    top_n = data.get("top_n", 3)

    # Validate types
    if not isinstance(user_id, int):
        return jsonify({"error": "user_id must be an integer"}), 400

    if not isinstance(top_n, int) or top_n < 1:
        return jsonify({"error": "top_n must be a positive integer"}), 400

    try:
        recommendations = recommend(user_id, top_n)

        return jsonify({
            "user_id": user_id,
            "recommendations": recommendations,
        })

    except ValueError as exc:
        logger.warning("Unknown user requested: %s", exc)
        return jsonify({"error": str(exc)}), 404

    except Exception as exc:
        logger.error("Recommendation failed: %s", exc, exc_info=True)
        return jsonify({"error": "Internal server error"}), 500


# ---------------------------------------------------------------------------
# Entrypoint
# ---------------------------------------------------------------------------
if __name__ == "__main__":
    debug_mode = os.environ.get("FLASK_DEBUG", "false").lower() == "true"
    port = int(os.environ.get("FLASK_PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=debug_mode)
