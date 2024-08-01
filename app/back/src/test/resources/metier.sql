INSERT INTO metier
VALUES ('MET001',
        'Fleuriste',
        'Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes et des accessoires de décoration. Il peut également être amené à conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en jardinerie ou en atelier de composition florale.',
        '[]'::jsonb);

INSERT INTO metier
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
        ]'::jsonb);

INSERT INTO metier
VALUES ('MET003',
        'Architecte',
        'L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de rénovation de bâtiments. Il peut travailler sur des projets de construction de maisons individuelles, d immeubles, de bureaux, d écoles, de musées, de centres commerciaux, de stades, etc. L architecte peut travailler en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité territoriale.',
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-metier/metiers/architecte"
          }
        ]'::jsonb);