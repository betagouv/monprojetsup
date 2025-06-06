INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, obsolete)
VALUES ('fl0001',
        'CAP Fleuriste',
        'Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 2 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.',
        'Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l''entretien des végétaux.',
        '{fleurs, jardin}',
        'Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.',
        'Le Certificat d''Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du système éducatif français, qui atteste l''acquisition d''une qualification professionnelle dans un métier précis. Il est généralement obtenu après une formation de deux ans suivant la fin du collège et s''adresse principalement aux élèves souhaitant entrer rapidement dans la vie active.',
        '{fl0010, fl0012}',
        ARRAY [0, 50, 0, 50, 0],
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste"
          }
        ]'::jsonb,
        false);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, obsolete)
VALUES ('fl0002',
        'Bac pro Fleuriste',
        'Le Bac pro Fleuriste est un diplôme de niveau 4 qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 3 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le Bac pro Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.',
        'Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l''entretien des végétaux.',
        '{fleurs, jardin, hortensia}',
        null,
        'Le Baccalauréat Professionnel, communément appelé Bac Pro, est un diplôme national de niveau 4 du système éducatif français. Il est conçu pour préparer les élèves à une insertion rapide et réussie dans le monde du travail tout en leur offrant la possibilité de poursuivre leurs études supérieures s''ils le souhaitent. Le Bac Pro se prépare généralement en trois ans après la classe de troisième, ou en deux ans après l''obtention d''un Certificat d''Aptitude Professionnelle (CAP).',
        '{fl0012}',
        ARRAY [13, 50, 12, 5, 15],
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste"
          }
        ]'::jsonb,
        true);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, obsolete)
VALUES ('fl0003',
        'ENSA',
        'L ENSA (École Nationale Supérieure d Architecture) est un établissement d enseignement supérieur qui forme des architectes. La formation dure 5 ans et est accessible après le Bac. Elle comprend des enseignements théoriques (histoire de l architecture, théorie de l architecture, etc.) et des enseignements pratiques (dessin, maquette, etc.). L ENSA permet d exercer le métier d architecte en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité territoriale.',
        '',
        '{}',
        '',
        '',
        null,
        ARRAY [12, 5, 15, 13, 50],
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/ensa"
          }
        ]'::jsonb,
        false);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, obsolete)
VALUES ('fl0004',
        'L1 - Histoire',
        'La licence se décline en une quarantaine de mentions, allant du droit, à l''informatique, en passant par les arts. Organisée en parcours types, définis par chaque université, la licence permet d''acquérir une culture générale solide, des compétences disciplinaires, transversales et linguistiques.',
        null,
        null,
        '',
        null,
        '{fl0005}',
        ARRAY [100, 0, 0, 0, 0],
        ' [
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire"
          }
        ]'::jsonb,
        false);

INSERT INTO ref_voeu (id, nom, commune, code_commune, latitude, longitude, obsolete)
VALUES ('ta0001', 'Lycée professionnel horticole de Montreuil', 'Montreuil', '93048', 48.861, 2.443, false);

INSERT INTO ref_voeu (id, nom, commune, code_commune, latitude, longitude, obsolete)
VALUES ('ta0002', 'ENSAPLV', 'Paris', '75119', 48.889, 2.393, true);

INSERT INTO ref_voeu (id, nom, commune, code_commune, latitude, longitude, obsolete)
VALUES ('ta0003', 'ENSA Nancy', 'Nancy', '54395', 48.692, 6.184, false);

INSERT INTO ref_voeu (id, nom, commune, code_commune, latitude, longitude, obsolete)
VALUES ('ta0004', 'ENSAB', 'Rennes', '35238', 48.117, 1.677, true);

INSERT INTO ref_voeu (id, nom, commune, code_commune, latitude, longitude, obsolete)
VALUES ('ta0005', 'Université Paris 1 Panthéon-Sorbonne', 'Paris', '75105', 48.846, 2.344, false);

INSERT INTO ref_join_formation_voeu(id_voeu, id_formation) VALUES ('ta0001', 'fl0001');
INSERT INTO ref_join_formation_voeu(id_voeu, id_formation) VALUES ('ta0002', 'fl0003');
INSERT INTO ref_join_formation_voeu(id_voeu, id_formation) VALUES ('ta0003', 'fl0003');
INSERT INTO ref_join_formation_voeu(id_voeu, id_formation) VALUES ('ta0004', 'fl0003');
INSERT INTO ref_join_formation_voeu(id_voeu, id_formation) VALUES ('ta0005', 'fl0004');