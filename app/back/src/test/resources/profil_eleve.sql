INSERT INTO ref_baccalaureat
VALUES ('Général',
        'Série Générale',
        'Générale');

INSERT INTO ref_baccalaureat
VALUES ('Professionnel',
        'Série Pro',
        'P');

INSERT INTO profil_eleve
VALUES (
        'AUCUNE_IDEE',
        'SECONDE',
        'Général',
        '{4, 1006}',
        '{animaux, agroequipement}',
        '{linguistique, voyage}',
        '{MET001}',
        'COURTE',
        'INDIFFERENT',
        '[
          {
            "codeInsee": "75115",
            "nom": "Paris",
            "latitude": 48.851227,
            "longitude": 2.2885659
          },
          {
            "codeInsee": "13055",
            "nom": "Marseille",
            "latitude": 43.300000,
            "longitude": 5.400000
          }
        ]'::jsonb,
        10.5,
        '{fl0001, fl0002}',
        '[
          {
            "idFormation": "fl0010",
            "priseDeNote": null,
            "niveauAmbition": 1,
            "voeuxChoisis": []
          },
          {
            "idFormation": "fl0012",
            "priseDeNote": "Mon voeu préféré",
            "niveauAmbition": 3,
            "voeuxChoisis": [
              "ta15974",
              "ta17831"
            ]
          }
        ]'::jsonb,
        '0f88ddd1-62ef-436e-ad3f-cf56d5d14c15');