import json
import re

def verifier_lycee_et_admins(nom_fichier_groups,nom_fichier_users):

    lycees_avec_referent = set()
    classes_avec_referent = set()
    classes_avec_plus_12_eleves = set()
    lycees_avec_proviseurs= set()
    lycees = set()

    dictionnaire_code = dict()

    liste_proviseurs = []
    with open(nom_fichier_users, 'r') as file:
        data = json.load(file)
        for entry in data:
            if entry.get("userType") == "proviseur":
                lycees_avec_proviseurs.add(entry.get('lycees')[0])
                liste_proviseurs.append(entry.get('pf').get('login'))
    # Ouvrir et charger le fichier JSON
    with open(nom_fichier_groups, 'r') as file:
        data = json.load(file)

    # Récupérer le tableau 'groups'
    groups = data.get('groups', [])

    # Expression régulière : 8 chiffres suivis d'une lettre
    regex_lycee = re.compile(r'\d+[a-zA-Z]')

    for group in groups:
        if 'lycee' in group and regex_lycee.match(str(group['lycee'])):
            # Vérifier si le champ 'lycee' respecte la regex
            lycee_value = str(group['lycee'])
            lycees.add(lycee_value)  # Ajouter la valeur de 'lycee' à l'ensemble des valeurs uniques
            id_classe = str(group['id'])

            if group.get("expeENSGroupe") == "T":
                dictionnaire_code.update({id_classe: group.get("registrationToken")})

    print(dictionnaire_code)
    print("\n")

    for group in groups:
        if 'lycee' in group and regex_lycee.match(str(group['lycee'])):
            # Vérifier si le champ 'lycee' respecte la regex

            if 'admins' in group and isinstance(group['admins'], list) and len(group['admins']) > 0:
                for admin in group['admins']:
                    if admin not in liste_proviseurs:
                        classes_avec_referent.add(group['id'])
                        lycees_avec_referent.add(group['lycee'])

            if 'members' in group and isinstance(group['members'], list) and len(group['members']) > 9:
                classes_avec_plus_12_eleves.add(group['id'])
                
    lycees_sans_referent = lycees - lycees_avec_referent

    print(f"Nombre de lycées pas inscrits : {len(lycees_sans_referent)} \n - Liste : {list(lycees_sans_referent)}")
    print(f"Nombre de lycées inscrits : {len(lycees_avec_referent)} \n - Liste : {list(lycees_avec_referent)}")
    print(f"Nombre de lycées avec proviseur : {len(lycees_avec_proviseurs)} \n - Liste : {list(lycees_avec_proviseurs)}")
    print("")
    print(f"Nombre de classes avec référent : {len(classes_avec_referent)} \n - Liste : {list(classes_avec_referent)}")
    print(f"Nombre de classes qui ont plus de 12 élèves : {len(classes_avec_plus_12_eleves)} \n - Liste : {list(classes_avec_plus_12_eleves)}")
            

# Utilisation de la fonction avec le nom du fichier en paramètre
nom_fichier_json_groups = 'groups.json'  # Remplacez 'votre_fichier.json' par le nom de votre fichier JSON
nom_fichier_json_users = 'usersExpeENS.json'  # Remplacez 'votre_fichier.json' par le nom de votre fichier JSON

verifier_lycee_et_admins(nom_fichier_json_groups,nom_fichier_json_users)
