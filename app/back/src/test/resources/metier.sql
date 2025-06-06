INSERT INTO ref_metier(id, label, descriptif_general, liens, obsolete)
VALUES ('MET001',
        'Fleuriste',
        'Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes et des accessoires de décoration. Il peut également être amené à conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en jardinerie ou en atelier de composition florale.',
        '[]'::jsonb, false);

INSERT INTO ref_metier(id, label, descriptif_general, liens, obsolete)
VALUES ('MET002',
        'Fleuriste événementiel',
        null,
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"
          },
          {
            "nom": "Voir la fiche HelloWork",
            "url": "https://www.hellowork.com/fr-fr/metiers/fleuriste.html"
          }
        ]'::jsonb, true);

INSERT INTO ref_metier(id, label, descriptif_general, liens, obsolete)
VALUES ('MET003',
        'Architecte',
        'L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de rénovation de bâtiments. Il peut travailler sur des projets de construction de maisons individuelles, d immeubles, de bureaux, d écoles, de musées, de centres commerciaux, de stades, etc. L architecte peut travailler en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité territoriale.',
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-metier/metiers/architecte"
          }
        ]'::jsonb, false);

INSERT INTO ref_formation (id, label)
VALUES ('fl660008', 'BTSA - Métiers du Végétal : Alimentation, Ornement, Environnement');

INSERT INTO ref_formation (id, label)
VALUES ('fl1', 'Classe préparatoire');

INSERT INTO ref_formation (id, label)
VALUES ('fl250', 'EA-BAC5 - Architecture');

INSERT INTO ref_formation (id, label)
VALUES ('fl10419', 'BTS - Architectures en Métal : conception et Réalisation');

INSERT INTO ref_join_formation_metier(id_formation, id_metier)
VALUES ('fl660008', 'MET002');

INSERT INTO ref_join_formation_metier(id_formation, id_metier)
VALUES ('fl10419', 'MET003');

INSERT INTO ref_join_formation_metier(id_formation, id_metier)
VALUES ('fl250', 'MET003');

