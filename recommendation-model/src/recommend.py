import os
import pickle
import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix

# Get base directory (recommendation-model folder)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

DATA_PATH = os.path.join(BASE_DIR, "data", "training-data.csv")
MODEL_PATH = os.path.join(BASE_DIR, "models", "svd_model.pkl")

def recommend(user_id, top_n=3):
    # Load data
    df = pd.read_csv(DATA_PATH)

    user_item_matrix = df.pivot_table(
        index='user_id',
        columns='trip_id',
        values='rating'
    ).fillna(0)

    # Load model
    with open(MODEL_PATH, "rb") as f:
        model = pickle.load(f)

    matrix = csr_matrix(user_item_matrix.values)
    latent_matrix = model.transform(matrix)

    user_vector = latent_matrix[user_id - 1]
    scores = user_vector.dot(model.components_)

    recommended_indices = np.argsort(scores)[::-1]

    trip_ids = user_item_matrix.columns.values
    recommended_trip_ids = trip_ids[recommended_indices]

    return recommended_trip_ids[:top_n]