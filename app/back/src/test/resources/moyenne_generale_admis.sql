INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('Générale', 'Bac Général', 'Générale', '1');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('STAV', 'Bac STAV', 'STAV', '2');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('ST2S', 'Bac ST2S', 'ST2S', '2');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('STMG', 'Bac STMG', 'STMG','2');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('STI2D', 'Bac STI2D', 'STI2D','2');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('STL', 'Bac STL', 'STL','2');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('P', 'Bac Professionnel', 'P', '3');
INSERT INTO ref_baccalaureat (id, nom, id_externe, id_carte_parcoursup) VALUES ('NC', 'Non-communiqué', 'NC', '0');

INSERT INTO ref_formation
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
        ]'::jsonb);

INSERT INTO ref_formation
VALUES ('fl0002',
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
        ]'::jsonb);

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

INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'ST2S', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,3,3,5,6,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'STAV', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'NC', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,2,6,7,14,17,21,24,29,32,32,34,35,36,36,36,36,36,36,36,36,36,36}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'STMG', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,3,4,5,5,5,5,5,5,5,5,5,5,5,5,5,5}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'P', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,3,5,5,6,6,8,10,10,10,10,10,10,10,10,10,10,10,10,10,10}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'Générale', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,3,3,3,5,6,6,6,8,9,10,10,10,10,10,10,10,10,10,10}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0001', 'STL', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2023', 'fl0002', 'NC', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,4,5,6,8,8,9,9,9,9,9,9,9,9,9,9,9,9}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2023', 'fl0002', 'Générale', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,2,3,4,5,5,6,6,6,6,6,6,6,6,6,6,6,6}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2023', 'fl0002', 'STMG', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2023', 'fl0002', 'P', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'Générale', '{0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,3,7,8,23,42,70,129,213,322,428,530,610,658,697,723,734,742,744,744,744,744}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'STL', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,2,2,3,3,3,3,3,3,3,3}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'STMG', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,2,2,2,4,4,4,4,4,4,4,4,4,4,4}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'P', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'NC', '{0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,3,7,9,24,44,74,135,220,334,444,552,634,682,722,749,760,769,771,771,771,771}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'STI2D', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}');
INSERT INTO ref_moyenne_generale_admis (annee, id_formation, id_bac, frequences_cumulees) VALUES ('2024', 'fl0003', 'ST2S', '{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2,2,3,7,10,13,15,15,15,15,15,16,16,16,16,16}');