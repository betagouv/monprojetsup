INSERT INTO ref_baccalaureat
VALUES ('Général', 'Série Générale', 'Générale', '1');

INSERT INTO ref_baccalaureat
VALUES ('Professionnel', 'Série Pro', 'P', '3');

INSERT INTO ref_baccalaureat
VALUES ('PA', 'Bac Pro Agricole', 'PA', '3');

INSERT INTO ref_specialite(id, label)
VALUES ('4',
        'Sciences de l''ingénieur'),
       ('5',
        'Biologie/Ecologie'),
       ('1006',
        'Economie et gestion hôtelière'),
       ('1008',
        'Enseignement scientifique alimentation - environnement'),
       ('1009',
        'Ressources humaines et communication'),
       ('1038',
        'Droit et Economie'),
       ('1040',
        'Physique-Chimie et Mathématiques'),
       ('1095',
        'Éducation Physique, Pratiques Et Culture Sportives');

INSERT INTO ref_join_baccalaureat_specialite(id_baccalaureat, id_specialite)
VALUES ('Professionnel', '4'),
       ('Général', '4'),
       ('Professionnel', '5'),
       ('Général', '5'),
       ('Professionnel', '1006'),
       ('Professionnel', '1008'),
       ('Professionnel', '1009'),
       ('Professionnel', '1038'),
       ('Général', '1038'),
       ('Général', '1040'),
       ('Professionnel', '1095');