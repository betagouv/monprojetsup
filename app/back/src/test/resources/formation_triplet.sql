INSERT INTO formation
VALUES ('fl0001', 'CAP Fleuriste', 'Le CAP Fleuriste est un diplôme de niveau V qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 2 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.', '{https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste}');

INSERT INTO formation
VALUES ('fl0002', 'Bac pro Fleuriste', 'Le Bac pro Fleuriste est un diplôme de niveau IV qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 3 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le Bac pro Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.', '{https://www.onisep.fr/ressources/univers-formation/formations/bac-pro-fleuriste}');

INSERT INTO formation
VALUES ('fl0003', 'ENSA', 'L ENSA (École Nationale Supérieure d Architecture) est un établissement d enseignement supérieur qui forme des architectes. La formation dure 5 ans et est accessible après le Bac. Elle comprend des enseignements théoriques (histoire de l architecture, théorie de l architecture, etc.) et des enseignements pratiques (dessin, maquette, etc.). L ENSA permet d exercer le métier d architecte en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité territoriale.', '{https://www.onisep.fr/ressources/univers-formation/formations/ensa}');

INSERT INTO formation
VALUES ('fl0004', 'L1 - Histoire', 'La licence se décline en une quarantaine de mentions, allant du droit, à l''informatique, en passant par les arts. Organisée en parcours types, définis par chaque université, la licence permet d''acquérir une culture générale solide, des compétences disciplinaires, transversales et linguistiques.', '{https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire}');

INSERT INTO triplet_affectation
VALUES ('ta0001', 'Lycée professionnel horticole de Montreuil', 'Montreuil', '93100', '{48.861, 2.443}', 'fl0001');

INSERT INTO triplet_affectation
VALUES ('ta0002', 'ENSAPLV', 'Paris', '75019', '{48.889, 2.393}', 'fl0003');

INSERT INTO triplet_affectation
VALUES ('ta0003', 'ENSA Nancy', 'Nancy', '54000', '{48.692, 6.184}', 'fl0003');

INSERT INTO triplet_affectation
VALUES ('ta0004', 'ENSAB', 'Rennes', '35000', '{48.117, 1.677}', 'fl0003');

INSERT INTO triplet_affectation
VALUES ('ta0005', 'Université Paris 1 Panthéon-Sorbonne', 'Paris', '75005', '{48.846, 2.344}', 'fl0004');