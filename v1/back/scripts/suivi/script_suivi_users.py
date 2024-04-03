import json
import re

nom_fichier = 'users_expeENS.json'
nom_fichier_groupe = 'groups.json'

# Ouvrir et charger le fichier JSON
with open(nom_fichier, 'r') as file:
    data = json.load(file)

lycees_avec_enseignant = set()
lycees_sans_enseignant = set()
classes_avec_enseignant = set()
classes_avec_psyen = set()
classes_avec_plus_12_eleves = set()
lycees_avec_proviseur = set()

dico_classes = dict()


for entry in data:
    classe_id = f"{entry.get('lycees')[0]}-{entry.get('classe')}"

    # Vérification du profil pour les classes avec enseignant, proviseur, et psyEN
    if entry.get("userType") == "pp":
        if entry.get("classe") is not None:
            classes_avec_enseignant.add(classe_id)
            lycees_avec_enseignant.add(entry.get('lycees')[0])
    elif entry.get("userType") == "proviseur":
        lycees_avec_proviseur.add(entry.get('lycees')[0])
    elif entry.get("userType") == "psyen":
        if entry.get("classe") is not None:
            classes_avec_psyen.add(classe_id)


for entry in data:
    classe_id = f"{entry.get('lycees')[0]}-{entry.get('classe')}"
    if entry.get("userType") == "lyceen":
        if(dico_classes.get(classe_id) is None):
            dico_classes[classe_id] = 1
        else:
            dico_classes[classe_id] += 1
        if(dico_classes.get(classe_id) > 12):
            classes_avec_plus_12_eleves.add(classe_id)

# Ouvrir et charger le fichier JSON
with open(nom_fichier_groupe, 'r') as file:
    data_2 = json.load(file)

    regex_lycee = re.compile(r'\d+[a-zA-Z]')

    for entry_2 in data_2.get('groups', []):
        lycee_id = entry_2.get('lycee')
        if regex_lycee.match(str(lycee_id)):
            lycees_sans_enseignant.add(lycee_id)

    lycees_sans_enseignant = lycees_sans_enseignant - lycees_avec_enseignant


print(f"Classes avec enseignant : {len(classes_avec_enseignant)} - Liste : {list(classes_avec_enseignant)}")
print(f"Classes avec PsyEN : {len(classes_avec_psyen)} - Liste : {list(classes_avec_psyen)}")
print(f"Classes avec plus de 12 élèves : {len(classes_avec_plus_12_eleves)} - Liste : {list(classes_avec_plus_12_eleves)}")
print(f"Lycées avec proviseur : {len(lycees_avec_proviseur)} - Liste : {list(lycees_avec_proviseur)}")

print(f"Lycées avec enseignant : {len(lycees_avec_enseignant)} - Liste : {list(lycees_avec_enseignant)}")
print(f"Lycées sans enseignant : {len(lycees_sans_enseignant)} - Liste : {list(lycees_sans_enseignant)}")
