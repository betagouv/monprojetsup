import json
from typing import Tuple

import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, Dataset
from tqdm import tqdm

from load_dataset import load_dataset

# -----------------
# CONFIGURATION
# -----------------
TRAINING_DATASET_FILE = "datasets/training_dataset_example.json"

OUTPUT_FILE = "outputs/results_dataset_example.json"

SCORES = [
    "geo",
    "duration",
    "app",
    "sim",
    "OFFRE_FORMATION",
    "fav",
    "typebac",
    "spec",
    "tags",
    "spec_bac_pro",
    "nbayes",
]  # List of score names, in the same order as in the dataset

NUMBER_OF_SCORES = len(SCORES)

EPOCHS = 1000
LEARNING_RATE = 0.01
BATCH_SIZE = 2**10

FIXED_BETA = {
    "geo": 0.9,
    "duration": 0.1,
}

DEFAULT_INIT_BETA = 0.5  # Default beta value for scores not in FIXED_BETA
# -----------------
# END CONFIGURATION
# -----------------

FIXED_MASK = torch.tensor([name in FIXED_BETA for name in SCORES], dtype=torch.bool)

INIT_BETAS = [
    FIXED_BETA[name] if name in FIXED_BETA else DEFAULT_INIT_BETA for name in SCORES
]

INIT_RAW_BETAS = torch.log(torch.tensor(INIT_BETAS) / (1 - torch.tensor(INIT_BETAS)))


class TrainingDataset(Dataset):
    """
    PyTorch Dataset class to represent the training dataset.

    Each data point consists of:
        - scores1: list of floats representing the individual scores of the first formation.
        - scores2: list of floats representing the individual scores of the second formation.
        - preference: a binary float (0.0 or 1.0) indicating which item is preferred.
    """

    def __init__(self, raw_data):
        self.scores1 = torch.tensor([d[0] for d in raw_data], dtype=torch.float32)
        self.scores2 = torch.tensor([d[1] for d in raw_data], dtype=torch.float32)
        self.preference = torch.tensor([d[2] for d in raw_data], dtype=torch.float32)

    def __len__(self):
        return len(self.preference)

    def __getitem__(self, idx):
        return self.scores1[idx], self.scores2[idx], self.preference[idx]


def initialize_parameters(init_raw_betas: torch.Tensor) -> nn.Parameter:
    """
    Initialize raw beta values as learnable PyTorch parameters.

    Args:
        init_raw_betas: tensor of initial raw beta values of shape (num_scores,).

    Returns:
        The learnable parameter to be optimized during training.
    """
    raw_beta = nn.Parameter(init_raw_betas.clone().float())
    return raw_beta


def compute_aggregated_scores(
    scores: torch.Tensor, raw_betas: torch.Tensor
) -> torch.Tensor:
    """
    Compute the aggregated score batch-wise.

    The formula applied per score dimension is:
        beta * (1 - score) + score
    Then all dimensions are multiplied together.

    Args:
        scores: input scores of shape (batch_size, num_scores).
        raw_betas: learnable raw beta values of shape (num_scores,).

    Returns:
        The aggregated scores as a tensor of shape (batch_size,).
    """
    betas = torch.sigmoid(raw_betas)
    weighted = betas * (1 - scores) + scores
    return weighted.prod(dim=-1)


def train_model(
    dataset: Dataset, raw_betas: nn.Parameter, learning_rate: float, epochs: int
) -> Tuple[nn.Parameter, float]:
    """
    Train the beta parameters using pairwise preferences and binary cross-entropy loss.

    Applies masking to prevent updates to fixed betas.

    Args:
        dataset: the training dataset.
        raw_betas: learnable parameters to optimize.
        learning_rate: learning rate for the Adam optimizer.
        epochs: number of training epochs.

    Returns:
        The learned raw beta parameters and the final average loss.
    """
    loader = DataLoader(dataset, batch_size=BATCH_SIZE, shuffle=True)
    optimizer = optim.Adam([raw_betas], lr=learning_rate)
    loss_fn = nn.BCELoss()

    length_dataset = float(len(dataset))

    for epoch in range(1, epochs + 1):
        print(f"\nStarting epoch {epoch}/{epochs}...")
        total_loss = 0.0
        for s1_batch, s2_batch, pref_batch in tqdm(loader, desc=f"Epoch {epoch}"):
            optimizer.zero_grad()
            sc1 = compute_aggregated_scores(s1_batch, raw_betas)
            sc2 = compute_aggregated_scores(s2_batch, raw_betas)
            pred = sc1 / (sc1 + sc2)
            loss = loss_fn(pred, pref_batch)
            loss.backward()

            # zero out gradients for fixed betas
            if raw_betas.grad is not None:
                raw_betas.grad[FIXED_MASK] = 0

            optimizer.step()
            total_loss += loss.item() * s1_batch.size(0)

        avg_loss = total_loss / length_dataset
        with torch.no_grad():
            beta_vals = torch.sigmoid(raw_betas).cpu().tolist()
        print(f"Epoch {epoch}/{epochs} - Avg Loss: {avg_loss:.4f}")
        print({name: beta_vals[i] for i, name in enumerate(SCORES)})

    return raw_betas, avg_loss


def main():
    raw_data = load_dataset(TRAINING_DATASET_FILE, NUMBER_OF_SCORES)
    training_dataset = TrainingDataset(raw_data)
    raw_betas = initialize_parameters(INIT_RAW_BETAS)
    learned_raw_betas, loss = train_model(
        training_dataset, raw_betas, LEARNING_RATE, EPOCHS
    )
    final_betas = torch.sigmoid(learned_raw_betas)

    output = {
        "raw_betas": {
            name: float(learned_raw_betas[i]) for i, name in enumerate(SCORES)
        },
        "betas": {name: float(final_betas[i]) for i, name in enumerate(SCORES)},
        "loss": loss,
        "config": {
            "training_dataset_file": TRAINING_DATASET_FILE,
            "scores": SCORES,
            "num_scores": NUMBER_OF_SCORES,
            "epochs": EPOCHS,
            "learning_rate": LEARNING_RATE,
            "batch_size": BATCH_SIZE,
            "fixed_beta": FIXED_BETA,
            "default_init_beta": DEFAULT_INIT_BETA,
        },
    }

    print_report(output)

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=4)

    print(f"Learned betas saved to {OUTPUT_FILE}")


def print_report(output: dict):
    """
    Print the final training results and configuration values.
    Args:
        output: a dictionary containing the training results and configuration values.
    """
    print()
    print()
    print("#" * 40)
    print("REPORT")
    print("#" * 40)
    print()
    print(json.dumps(output, indent=4))
    print()


if __name__ == "__main__":
    main()
