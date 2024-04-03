import json
import csv

# Open the file in write mode


def load_json(filename):
    with open(filename, "r") as f:
        data = json.load(f)
    return data


groups = load_json("groups.json")

with open("codes.csv", "w", newline="") as file:
    writer = csv.writer(file)

    # Write the header
    writer.writerow(["name", "lycee", "classe", "registrationToken", "adminToken"])

    # Write the data
    for group in groups["groups"]:
        writer.writerow(
            [
                group.get("name", None),
                group.get("lycee", None),
                group.get("classe", None),
                group.get("registrationToken", None),
                group.get("adminToken", None),
            ]
        )
