INSERT INTO ref_domaine_categorie
VALUES ('agriculture_alimentaire',
        'Agriculture et Alimentation',
        '🥕');

INSERT INTO ref_domaine_categorie
VALUES ('commerce', 'Commerce', '🏢');

INSERT INTO ref_domaine
VALUES ('animaux',
        'Soins aux animaux',
        'agriculture_alimentaire',
        '🐮',
        'Pour travailler dans les élevages ou la pêche, mais aussi apprendre à soigner les animaux, les nourrir et assurer leur bien-être.');

INSERT INTO ref_domaine
VALUES ('agroequipement',
        'Agroéquipement',
        'agriculture_alimentaire',
        '🚜',
        null);