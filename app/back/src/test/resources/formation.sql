INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
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
          },
          {
            "nom": "Voir la fiche France Travail",
            "url": "https://candidat.francetravail.fr/formations/detail/3139962/true"
          }
        ]'::jsonb,
        false,
        false);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
VALUES ('fl0002',
        'Bac pro Fleuriste',
        'Le Bac pro Fleuriste est un diplôme de niveau 4 qui permet d acquérir les compétences nécessaires pour exercer le métier de fleuriste. La formation dure 3 ans et est accessible après la classe de 3ème. Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le Bac pro Fleuriste permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.',
        'Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l''entretien des végétaux.',
        '{fleurs, jardin, hortensia}',
        null,
        'Le Baccalauréat Professionnel, communément appelé Bac Pro, est un diplôme national de niveau 4 du système éducatif français. Il est conçu pour préparer les élèves à une insertion rapide et réussie dans le monde du travail tout en leur offrant la possibilité de poursuivre leurs études supérieures s''ils le souhaitent. Le Bac Pro se prépare généralement en trois ans après la classe de troisième, ou en deux ans après l''obtention d''un Certificat d''Aptitude Professionnelle (CAP).',
        '{fl0012}',
        ARRAY [13, 50, 12, 5, 15],
        '[]'::jsonb,
        true, false);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
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
          },
          {
            "nom": "Voir les formations",
            "url": "https://www.culture.gouv.fr/Thematiques/Architecture/Formations-recherche-et-metiers/Les-formations-d-architecte-et-de-paysagiste/Les-cursus-et-les-diplomes/Les-ecoles-nationales-superieures-d-architecture"
          }
        ]'::jsonb,
        false, false);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
VALUES ('fl0005',
        'L1 - Géographie',
        'La licence de géographie est un cursus universitaire qui explore les interactions entre les environnements naturels et les sociétés humaines. Elle couvre des domaines variés comme la cartographie, la géopolitique, et l''aménagement du territoire. Les diplômés peuvent poursuivre des carrières dans l''urbanisme, l''environnement, la recherche, et l''enseignement.',
        null,
        '{histoire}',
        '',
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire"
          }
        ]'::jsonb,
        true, true);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
VALUES ('fl0004',
        'L1 - Histoire',
        'La licence se décline en une quarantaine de mentions, allant du droit, à l''informatique, en passant par les arts. Organisée en parcours types, définis par chaque université, la licence permet d''acquérir une culture générale solide, des compétences disciplinaires, transversales et linguistiques.',
        null,
        null,
        '',
        null,
        '{fl0005}',
        ARRAY [100, 0, 0, 0, 0],
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire"
          }
        ]'::jsonb, false, true);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
VALUES ('fl0006',
        'L1 - Histoire de l''art',
        'La licence se décline en une quarantaine de mentions, allant du droit, à l''informatique, en passant par les arts. Organisée en parcours types, définis par chaque université, la licence permet d''acquérir une culture générale solide, des compétences disciplinaires, transversales et linguistiques.',
        null,
        null,
        '',
        null,
        null,
        ARRAY [100, 0, 0, 0, 0],
        '[
          {
            "nom": "Voir la fiche Onisep",
            "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire-de-l-art"
          }
        ]'::jsonb,
        true, false);

INSERT INTO ref_formation(id, label, descriptif_general, descriptif_attendu, mots_clefs, descriptif_conseils, descriptif_diplome, formations_psup, criteres_analyse, liens, apprentissage, obsolete)
VALUES ('fl240',
        'Ecoles de commerce (Bac+5)',
        'Les écoles de commerce et de management sont des formations souvent privées, que tu peux intégrer directement après le bac ou après une prépa (pour les plus prestigieuses). Elles proposent de te former aux métiers du secteur tertiaire, en offrant des formations qui varient fortement en fonction de l''école. Il faut donc te renseigner en amont pour savoir vers quelle école tu souhaites te tourner, car elles n''ont pas toutes le même niveau de réputation. Et les plus demandées necessitent de passer des concours pour lesquels il faut te préparer très sérieusement. Au programme, une fois entré, tu auras des cours de management, d''économie, de marketing, de communication etc. Ce sont des formations généralistes, mais qui ouvrent aussi beaucoup de voies sur le marché du travail, elles te promettent donc de belles carrières.',
        'L’ouverture et la curiosité, notamment en s’intéressant aux enjeux contemporains (environnement économique, entreprises, enjeux sociétaux, etc.), la capacité à prendre du recul et des responsabilités, et un intérêt pour la gestion sont des qualités recherchées par les écoles de commerce et de management pour leurs futurs étudiants. Elles recherchent par ailleurs une grande diversité de profils, également attendue par les entreprises, rendant les attendus nationaux assez larges en termes de parcours scolaire antérieur et de centres d''intérêt.  L’étudiant en école de commerce et de management doit disposer de compétences en :  - expression écrite et orale afin de pouvoir défendre un argumentaire précis et présenter un projet  - langues étrangères, a minima en anglais, afin d’être capable de lire, écrire et s’exprimer à l’écrit et à l’oral et de travailler à terme dans un contexte international  - culture générale et humanités, faire preuve d’ouverture d’esprit et de sensibilité aux enjeux de la société et de l’économie  Ces compétences peuvent être attestées par les résultats obtenus aux épreuves de baccalauréat ainsi que dans les évaluations communes durant les années de lycée. Elles peuvent également être appréciées à l’occasion d’épreuves propres au processus de sélection (écrites, orales), voire d’entretiens.',
        null,
        '',
        null,
        null,
        ARRAY [22,36,16,14,10],
        '[
            {
                "nom": "Infos Onisep",
                "url": "https://avenirs.onisep.fr/formation/les-principaux-domaines-de-formation/les-ecoles-de-commerce"
            },
            {
                "nom": "Voir sur la carte Parcoursup - Formation des écoles de commerce et de management Bac + 5",
                "url": "https://dossier.parcoursup.fr/Candidat/carte?search=formation+des+ecoles+de+commerce+et+de+management+bac+++5"
                }
        ]'::jsonb,
        true, false);

