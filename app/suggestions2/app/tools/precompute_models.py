#!/usr/bin/env python
"""
Script to precompute and store the NaiveBayes models.

This script connects to the database, computes all three models
(profil_expert, profil_lyceen, voeux_parcoursup) and saves them to disk.
The saved models can then be loaded at startup instead of recomputing them each time.

Usage:
    python -m app.tools.precompute_models [--output-dir <path>]

By default the models are saved in DEFAULT_MODELS_DIR (from app.config)
"""
import argparse
import time
from pathlib import Path

from app.adapters.naive_bayes import NaiveBayesMatrix
from app.adapters.pg_database import PostgresDatabase
from app.config import CONFIG, DEFAULT_MODELS_DIR
from app.db_schema import DbTables


def precompute_model(
    name: str,
    profiles_iterator,
    output_dir: Path,
) -> float:
    """
    Compute a single NaiveBayes model and save it to disk.
    
    Args:
        name: Name of the model
        profiles_iterator: Iterator of profiles batches
        output_dir: Directory where to save the model files
        
    Returns:
        Computation time in seconds
    """
    print(f"\n--- Computing '{name}' model ---")
    start_time = time.time()
    
    service = NaiveBayesMatrix(regularization_laplace=1.0)
    service.init_from_profiles_batched(profiles_iterator)
    
    computation_time = time.time() - start_time
    print(f"   Computed in {computation_time:.2f}s")
    print(f"   Matrix shape: {service.matrix.shape}")

    service.save_to_file(directory=output_dir, name=name)
    
    return computation_time


def precompute_all_models(output_dir: Path) -> None:
    """
    Compute all NaiveBayes models and save them to disk.
    
    Args:
        output_dir: Directory where to save the model files
    """
    print("=" * 60)
    print("Precomputing all NaiveBayes models")
    print("=" * 60)
    print(f"Tables: expert={DbTables.REF_EXPERT}, lyceen={DbTables.REF_LYCEEN}")
    print(f"        paniers={DbTables.PANIERS_VOEUX}, join={DbTables.JOIN_FORMATION_VOEU}")
    
    print("\nConnecting to database...")
    start_time = time.time()
    data_repo = PostgresDatabase.from_env()
    print(f"Connected in {time.time() - start_time:.2f}s")
    
    total_time = 0.0
    
    total_time += precompute_model(
        name="profil_expert",
        profiles_iterator=data_repo.iter_profiles_batched(
            table_name=DbTables.REF_EXPERT,
            config=CONFIG,
        ),
        output_dir=output_dir,
    )
    
    total_time += precompute_model(
        name="profil_lyceen",
        profiles_iterator=data_repo.iter_profiles_batched(
            table_name=DbTables.REF_LYCEEN,
            config=CONFIG,
        ),
        output_dir=output_dir,
    )
    
    total_time += precompute_model(
        name="voeux_parcoursup",
        profiles_iterator=data_repo.iter_paniers_voeux_as_profiles_batched(
            paniers_table=DbTables.PANIERS_VOEUX,
            join_table=DbTables.JOIN_FORMATION_VOEU,
        ),
        output_dir=output_dir,
    )
    
    print("\n" + "=" * 60)
    print("Done!")
    print(f"Total computation time: {total_time:.2f}s")
    print(f"Models saved to: {output_dir}")
    print("=" * 60)


def main():
    parser = argparse.ArgumentParser(
        description="Precompute and store all NaiveBayes models"
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=DEFAULT_MODELS_DIR,
        help=f"Directory where to save the model files (default: {DEFAULT_MODELS_DIR})",
    )
    
    args = parser.parse_args()
    precompute_all_models(args.output_dir)


if __name__ == "__main__":
    main()
