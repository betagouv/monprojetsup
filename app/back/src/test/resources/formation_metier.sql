INSERT INTO metier
VALUES ('MET001', 'Fleuriste', 'Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes et des accessoires de décoration. Il peut également être amené à conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en jardinerie ou en atelier de composition florale.', '{https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste}');

INSERT INTO metier
VALUES ('MET002', 'Fleuriste événementiel', 'Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes et des accessoires de décoration pour des événements particuliers (mariages, baptêmes, anniversaires, réceptions, etc.). Il peut également être amené à conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste événementiel peut travailler en boutique, en grande surface, en jardinerie ou en atelier de composition florale.', '{https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste}');

INSERT INTO metier
VALUES ('MET003', 'Architecte', 'L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de rénovation de bâtiments. Il peut travailler sur des projets de construction de maisons individuelles, d immeubles, de bureaux, d écoles, de musées, de centres commerciaux, de stades, etc. L architecte peut travailler en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité territoriale.', '{https://www.onisep.fr/ressources/univers-metier/metiers/architecte}');

INSERT INTO formation
VALUES ('fl0001', 'CAP Fleuriste', 'Le CAP Fleuriste est un diplôme de niveau V qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 2 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.', '{https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste}');

INSERT INTO formation
VALUES ('fl0002', 'Bac pro Fleuriste', 'Le Bac pro Fleuriste est un diplôme de niveau IV qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 3 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le Bac pro Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.', '{https://www.onisep.fr/ressources/univers-formation/formations/bac-pro-fleuriste}');

INSERT INTO formation
VALUES ('fl0003', 'ENSA', 'L ENSA (École Nationale Supérieure d Architecture) est un établissement d enseignement supérieur qui forme des architectes. La formation dure 5 ans et est accessible après le Bac. Elle comprend des enseignements théoriques (histoire de l architecture, théorie de l architecture, etc.) et des enseignements pratiques (dessin, maquette, etc.). L ENSA permet d exercer le métier d architecte en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité territoriale.', '{https://www.onisep.fr/ressources/univers-formation/formations/ensa}');

INSERT INTO formation
VALUES ('fl0004', 'L1 - Histoire', 'La licence se décline en une quarantaine de mentions, allant du droit, à l''informatique, en passant par les arts. Organisée en parcours types, définis par chaque université, la licence permet d''acquérir une culture générale solide, des compétences disciplinaires, transversales et linguistiques.', '{https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire}');

INSERT INTO join_metier_formation
VALUES ('MET001', 'fl0001');

INSERT INTO join_metier_formation
VALUES ('MET001', 'fl0002');

INSERT INTO join_metier_formation
VALUES ('MET002', 'fl0001');

INSERT INTO join_metier_formation
VALUES ('MET002', 'fl0002');

INSERT INTO join_metier_formation
VALUES ('MET003', 'fl0003');