import requests
import os
import csv

from dotenv import load_dotenv

# charger le csv
experts = []
with open("experts.csv", mode="r") as file:
    csv_reader = csv.DictReader(file)  # Utiliser DictReader pour lire avec les en-têtes
    for row in csv_reader:
        experts.append(
            [
                row["EMAIL"],
                row["NOM"],
                row["PRENOM"],
            ]
        )


# Load environment variables from .env file
load_dotenv()

# Retrieve environment variables
keycloak_url = os.getenv("KEYCLOAK_URL")
realm_name = os.getenv("REALM_NAME")
client_id = os.getenv("CLIENT_ID")
admin_username = os.getenv("ADMIN_USERNAME")
admin_password = os.getenv("ADMIN_PASSWORD")
experts_password = os.getenv("EXPERTS_PASSWORD")

# Obtenir le token d'accès
token_url = f"{keycloak_url}/realms/{realm_name}/protocol/openid-connect/token"
token_data = {
    "client_id": client_id,
    "username": admin_username,
    "password": admin_password,
    "grant_type": "password",
}
token_headers = {
    "Content-Type": "application/x-www-form-urlencoded",
}

# Envoyer la requête pour obtenir le token
response = requests.post(token_url, data=token_data, headers=token_headers)
if response.status_code == 200:
    access_token = response.json()["access_token"]
    print("Access Token obtained successfully!")
else:
    print(f"Failed to obtain token: {response.text}")
    exit(1)


# Créer un nouvel utilisateur
create_user_url = f"{keycloak_url}/admin/realms/{realm_name}/users"
for expert in experts:
    email = expert[0]
    nom = expert[1]
    prenom = expert[2]

    for idx in range(1, 11):

        username = f"{email}_{idx}"
        # Check if the user exists
        user_search_url = f"{keycloak_url}/admin/realms/{realm_name}/users"
        search_params = {"username": username}
        search_headers = {
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json",
        }

        # Send GET request to search for the user
        search_response = requests.get(user_search_url, headers=search_headers, params=search_params)
        if search_response.status_code == 200:
            users = search_response.json()
            if users:
                print(f"User '{username}' already exists.")
                continue

        print(f"Creating {username}")

        # Ajouter le token dans les headers pour l'authentification
        create_user_headers = {
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json",
        }

        user_data = {
            "username": f"{username}",  # Nom d'utilisateur
            "email": f"{email}",  # Email de l'utilisateur
            "firstName": f"{prenom}",
            "lastName": f"{nom}",
            "enabled": True,  # Activer le compte
            "emailVerified": True,
            "attributes": {"profile": "expert"},
            "credentials": [
                {
                    "type": "password",
                    "value": f"{experts_password}",
                    "temporary": False,
                }
            ],
        }

        # Envoyer la requête pour créer l'utilisateur
        create_user_response = requests.post(
            create_user_url, json=user_data, headers=create_user_headers
        )
        if create_user_response.status_code != 201:
            print(f"Failed to create user: {create_user_response.text}")
