from typing import Literal
import argparse
import db
import numpy as np


def encode_formation(f: dict[str, str]) -> str:
    return f["label"] + " : " + f["descriptif_general"]


def encode_domaine(d: dict[str, str]) -> str:
    return d["nom"] + " : " + d["description"]


def compute_similarities(
    model_name: str,
    forms_text: list[str],
    doms_text: list[str],
    similarity_fn: Literal["cosine", "dot", "euclidean", "manhattan"] = "dot",
):
    # This import takes a long time,
    # so we put it here to avoid slowing down the script
    from sentence_transformers import SentenceTransformer

    model = SentenceTransformer(model_name)
    model.similarity_fn_name = similarity_fn

    forms_embedding = model.encode(forms_text)
    doms_embedding = model.encode(doms_text)

    similarities = model.similarity(forms_embedding, doms_embedding)
    return similarities


def get_best_edges(
    sims: np.ndarray,
    forms: list[dict[str, str]],
    doms: list[dict[str, str]],
    edges: set[tuple[str, str]],
    max_count: int,
    top_k: int,
) -> list[tuple[tuple[str, str], float]]:
    assert sims.shape == (len(forms), len(doms))
    res = []
    for dom, row in zip(doms, sims.T):
        dom_id = dom["id"]
        assert len(row) == len(forms)

        indices = np.argsort(row)
        cur_count = 0
        for j in list(indices[-top_k:])[::-1]:
            form_id = forms[j]["id"]
            form_name = forms[j]["label"]
            is_existing_edge = (form_id, dom_id) in edges

            if not is_existing_edge and cur_count < max_count:
                print("new edge", form_name, dom["nom"], row[j])
                cur_count += 1
                res.append(((form_id, dom_id), row[j]))
    return res


def output_edges(
    edges: list[tuple[tuple[str, str], float]],
    filename: str,
    formation_dict: dict[str, dict[str, str]],
    domaine_dict: dict[str, dict[str, str]],
):
    with open(filename, "w+") as f:
        f.write("score;domaine;formation\n")
        for (form_id, dom_id), score in edges:
            dom_name = domaine_dict[dom_id]["nom"]
            form_name = formation_dict[form_id]["label"]
            f.write(f"{score};{dom_name};{form_name}\n")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        prog="DataAugmenter",
        description="Suggest new edges (formation-domaine) for the suggestion API using transformers.",
    )

    parser.add_argument(
        "-n",
        "--max-count",
        type=int,
        default=2,
        help="Return at most this number of edges suggestions per domaine. (Default: 2)",
    )
    parser.add_argument(
        "-k",
        "--top-k",
        type=int,
        default=10,
        help="Only return edges between a domaine and the top `TOP_K` formations for that domaine. (Default: 10)",
    )
    parser.add_argument(
        "-c",
        "--cosine",
        action="store_true",
        help="Use cosine similarity instead of the dot product.",
    )
    parser.add_argument(
        "-o",
        "--output",
        default="results.csv",
        help="Path of the output file. (Default: 'results.csv')",
    )
    args = parser.parse_args()

    ## Load data
    print("Loading data...")
    conn = db.connect_to_db()

    forms_dict = db.load_formations(conn)
    forms = [forms_dict[f] for f in forms_dict]
    forms_text = [encode_formation(f) for f in forms]

    doms_dict = db.load_domaines(conn)
    doms = [doms_dict[d] for d in doms_dict]
    doms_text = [encode_domaine(d) for d in doms]

    edges = db.load_edges(conn)

    ## Compute similarities
    print("Computing similarities...")
    model_names = [
        "paraphrase-multilingual-mpnet-base-v2",
        "paraphrase-multilingual-MiniLM-L12-v2",
    ]

    sims = [
        compute_similarities(
            m, forms_text, doms_text, similarity_fn="cosine" if args.cosine else "dot"
        )
        for m in model_names
    ]
    s = sum(sims) / len(sims)

    ## Compute best edges
    print("Computing new edges...")
    new_edges = get_best_edges(s, forms, doms, edges, args.max_count, args.top_k)
    output_edges(
        new_edges,
        args.output,
        forms_dict,
        doms_dict,
    )

    print(f"New edges written to''{args.output}'")
    print("Done!")
