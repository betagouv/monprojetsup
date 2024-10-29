INSERT INTO ref_formation
VALUES ('fl0001',
        'CAP Fleuriste',
        null,
        null,
        '{fleurs, jardin}',
        null,
        null,
        null,
        ARRAY [0, 50, 0, 50, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0002',
        'Bac pro Fleuriste',
        null,
        null,
        '{fleurs, jardin, hortensia}',
        null,
        null,
        null,
        ARRAY [13, 50, 12, 5, 15],
        null);

INSERT INTO ref_formation
VALUES ('fl0003',
        'ENSA',
        null,
        null,
        '{architecture}',
        null,
        null,
        null,
        ARRAY [13, 50, 12, 5, 15],
        null);

INSERT INTO ref_formation
VALUES ('fl0005',
        'L1 - Géographie',
        null,
        null,
        '{histoire}',
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0004',
        'L1 - Histoire',
        null,
        null,
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0006',
        'L1 - Histoire de l''art',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0007',
        'DEUST - Technicien en qualité et distribution des produits alimentaires',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0008',
        'BUT Numerique',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0009',
        'but',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0010',
        'BUT Numérique',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0011',
        'BÙT',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0012',
        'Classe préparatoire aux études supérieures - Cinéma audiovisuel',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation
VALUES ('fl0013',
        'L1 - Sciences sanitaires et sociales -  Accès Santé (LAS)',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, obsolete)
VALUES ('fl0015',
        'Licence - Géographie et aménagement)',
        null,
        '{}',
        null,
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null, true);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, obsolete)
VALUES ('fl0016',
        'Bachelors des écoles d''ingénieurs (Bac+3))',
        null,
        '{}',
        '{géographie}',
        null,
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        null, true);