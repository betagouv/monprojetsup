INSERT INTO ref_baccalaureat
VALUES ('Général',
        'Série Générale',
        'Générale');

INSERT INTO ref_baccalaureat
VALUES ('Professionnel',
        'Série Pro',
        'P');

INSERT INTO profil_eleve
VALUES ('AUCUNE_IDEE',
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
            "niveauAmbition": 1
          },
          {
            "idFormation": "fl0012",
            "priseDeNote": "Ma formation préférée",
            "niveauAmbition": 3
          }
        ]'::jsonb,
        '0f88ddd1-62ef-436e-ad3f-cf56d5d14c15',
        '[
            {"idVoeu":"ta15974", "estFavoriParcoursup": "true" },
            {"idVoeu":"ta17831", "estFavoriParcoursup": "false" }
        ]'::jsonb
        );

INSERT INTO profil_eleve
VALUES ('QUELQUES_PISTES',
        'TERMINALE',
        'Professionnel',
        '{}',
        '{animaux, agroequipement}',
        '{linguistique, voyage}',
        '{MET002}',
        'LONGUE',
        'TRES_INTERESSE',
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
        '{
          fl0001,
          fl0002
        }',
        '[
          {
            "idFormation": "fl0010",
            "priseDeNote": null,
            "niveauAmbition": 1
          },
          {
            "idFormation": "fl0012",
            "priseDeNote": "Ma formation préférée",
            "niveauAmbition": 3
          }
        ]'::jsonb,
        '129f6d9c-0f6f-4fa4-8107-75b7cb129889',
        '[
            {"idVoeu":"ta15974", "estFavoriParcoursup": "true" },
            {"idVoeu":"ta17831", "estFavoriParcoursup": "false" }
        ]'::jsonb
        );

INSERT INTO eleve_compte_parcoursup
VALUES ('12345', '2024-09-27', '0f88ddd1-62ef-436e-ad3f-cf56d5d14c15')