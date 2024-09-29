import requests
import os

from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Retrieve environment variables
keycloak_url = os.getenv("KEYCLOAK_URL")
realm_name = os.getenv("REALM_NAME")
client_id = os.getenv("CLIENT_ID")
admin_username = os.getenv("ADMIN_USERNAME")
admin_password = os.getenv("ADMIN_PASSWORD")

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
user_data = {
    "username": "newuser",  # Nom d'utilisateur
    "email": "newuser@example.com",  # Email de l'utilisateur
    "firstName": "New",
    "lastName": "User",
    "enabled": True,  # Activer le compte
}

# Ajouter le token dans les headers pour l'authentification
create_user_headers = {
    "Authorization": f"Bearer {access_token}",
    "Content-Type": "application/json",
}

# Envoyer la requête pour créer l'utilisateur
create_user_response = requests.post(create_user_url, json=user_data, headers=create_user_headers)
if create_user_response.status_code == 201:
    print("User created successfully!")
else:
    print(f"Failed to create user: {create_user_response.text}")
    
