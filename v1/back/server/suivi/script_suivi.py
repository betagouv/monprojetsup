import json
import re

def verifier_lycee_et_admins(nom_fichier):
    # Ouvrir et charger le fichier JSON
    with open(nom_fichier, 'r') as file:
        data = json.load(file)

    # Récupérer le tableau 'groups'
    groups = data.get('groups', [])

    compteur = 0  # Initialiser le compteur
    lycees_uniques_vides = set()  # Pour stocker les valeurs uniques de 'lycee'
    lycees_uniques_non_vides = set()  # Pour stocker les valeurs uniques de 'lycee'

    # Expression régulière : 8 chiffres suivis d'une lettre
    regex_lycee = re.compile(r'\d+[a-zA-Z]')

    for group in groups:
        if 'lycee' in group and regex_lycee.match(str(group['lycee'])):
            # Vérifier si le champ 'lycee' respecte la regex
            lycee_value = str(group['lycee'])
            lycees_uniques_vides.add(lycee_value)  # Ajouter la valeur de 'lycee' à l'ensemble des valeurs uniques
    print("Liste des classes inscrites :")
    for group in groups:
        if 'lycee' in group and regex_lycee.match(str(group['lycee'])):
            # Vérifier si le champ 'lycee' respecte la regex

            if 'admins' in group and isinstance(group['admins'], list) and len(group['admins']) > 0:
                # Vérifier si le champ 'admins' n'est pas un tableau vide
                print(group['name'])  # Afficher le champ 'name'
                compteur += 1  # Incrémenter le compteur
                lycee_value = str(group['lycee'])
                if lycees_uniques_vides.__contains__(lycee_value):
                    lycees_uniques_vides.remove(lycee_value)
                    lycees_uniques_non_vides.add(lycee_value)

    print("")
    # Afficher le nombre d'admins non vides
    print(f"Nombre de classes inscrites : {compteur}")
    print("")
    # Afficher le nombre de valeurs uniques observées dans 'lycee'
    print(f"Nombre de lycées qui se sont inscrits : {len(lycees_uniques_non_vides)}")
    # Afficher les champs 'lycee' quand 'admins' est un tableau vide
    print("UAI des lycées qui sont inscrits sur la plateforme :")
    for lycee in lycees_uniques_non_vides:
        print(lycee)
    print("")



    # Afficher le nombre de valeurs uniques observées dans 'lycee'
    print(f"Nombre de lycées qui ne sont pas inscrits : {len(lycees_uniques_vides)}")
    # Afficher les champs 'lycee' quand 'admins' est un tableau vide
    print("UAI des lycées qui ne sont pas du tout inscrits sur la plateforme :")
    for lycee in lycees_uniques_vides:
        print(lycee)
    print("")

# Utilisation de la fonction avec le nom du fichier en paramètre
nom_fichier_json = 'groups.json'  # Remplacez 'votre_fichier.json' par le nom de votre fichier JSON
verifier_lycee_et_admins(nom_fichier_json)
