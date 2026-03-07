import os
import pickle
import logging

import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix

logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_PATH = os.path.join(BASE_DIR, "data", "training-data.csv")
MODEL_PATH = os.path.join(BASE_DIR, "models", "svd_model.pkl")

# ---------------------------------------------------------------------------
# Load data and model ONCE at import time (not on every request)
# ---------------------------------------------------------------------------
logger.info("Loading training data from %s", DATA_PATH)
_df = pd.read_csv(DATA_PATH)

_user_item_matrix = _df.pivot_table(
    index="user_id",
    columns="trip_id",
    values="rating"
).fillna(0)

logger.info("Loading SVD model from %s", MODEL_PATH)
with open(MODEL_PATH, "rb") as _f:
    _model = pickle.load(_f)

_sparse_matrix = csr_matrix(_user_item_matrix.values)
_latent_matrix = _model.transform(_sparse_matrix)

# Build a mapping from user_id → row index in the latent matrix
_user_id_to_index = {
    uid: idx for idx, uid in enumerate(_user_item_matrix.index)
}

_trip_ids = _user_item_matrix.columns.values

logger.info(
    "Recommendation engine ready — %d users, %d trips",
    len(_user_id_to_index),
    len(_trip_ids),
)


def recommend(user_id, top_n=3):
    """
    Return a list of recommended trip IDs for the given user.

    Args:
        user_id: Integer user ID (must exist in training data).
        top_n:   Number of recommendations to return (default 3).

    Returns:
        List[int] of recommended trip IDs, sorted by predicted score.

    Raises:
        ValueError: If user_id is not found in the training data.
    """
    # Validate user_id
    if user_id not in _user_id_to_index:
        raise ValueError(
            f"User ID {user_id} not found in training data. "
            f"Known user IDs: {sorted(_user_id_to_index.keys())}"
        )

    # Clamp top_n to a valid range
    top_n = max(1, min(top_n, len(_trip_ids)))

    # Look up user row by actual ID (not by index arithmetic)
    row_index = _user_id_to_index[user_id]
    user_vector = _latent_matrix[row_index]

    # Calculate predicted scores for all trips
    scores = user_vector.dot(_model.components_)

    # Sort trips by descending score
    recommended_indices = np.argsort(scores)[::-1]
    recommended_trip_ids = _trip_ids[recommended_indices]

    # Return as a plain Python list of ints
    return [int(tid) for tid in recommended_trip_ids[:top_n]]
