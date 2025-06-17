import json
from typing import List, Tuple


def load_dataset(
    file_path: str, number_of_scores: int
) -> List[Tuple[List[float], List[float], float]]:
    """
    Load training data from a JSON file.

    Each entry in the JSON is a tuple: (scores1, scores2, preference).
    This function filters out entries with incorrect lengths or invalid preferences.

    Args:
        file_path: Path to the JSON file containing the dataset.

    Returns:
        The filtered dataset.
    """
    with open(file_path, "r", encoding="utf-8") as f:
        raw = json.load(f)

    filtered = [
        (s1, s2, p)
        for s1, s2, p in raw
        if len(s1) == number_of_scores
        and len(s2) == number_of_scores
        and p in (0.0, 1.0)
    ]
    return filtered
