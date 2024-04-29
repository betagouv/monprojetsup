const pages = {
  ACCUEIL: "Accueil",
  ARTICLES: "Articles",
  PLAN_DU_SITE: "Plan du site",
  ACCESSIBILITÉ: "Accessibilité: non conforme",
  MENTIONS_LÉGALES: "Mentions légales",
  DONNÉES_PERSONNELLES: "Données personnelles",
  GESTION_COOKIES: "Gestion des cookies",
} as const;

const app = {
  NOM: "Mon Projet Sup",
  DESCRIPTION: "Le guide qui facilite l’orientation des lycéens",
} as const;

export const localeFR = {
  APP: {
    NOM: app.NOM,
  },
  ENTÊTE: {
    DESCRIPTION_SERVICE: app.DESCRIPTION,
  },
  PIED_DE_PAGE: {
    DESCRIPTION_SERVICE: app.DESCRIPTION,
    LIENS_INTERNES: {
      PLAN_DU_SITE: pages.PLAN_DU_SITE,
      ACCESSIBILITÉ: pages.ACCESSIBILITÉ,
      MENTIONS_LÉGALES: pages.MENTIONS_LÉGALES,
      DONNÉES_PERSONNELLES: pages.DONNÉES_PERSONNELLES,
      GESTION_COOKIES: pages.GESTION_COOKIES,
    },
  },
  NAVIGATION: {
    ACCUEIL: pages.ACCUEIL,
    ARTICLES: pages.ARTICLES,
    MAIN_NAVIGATION: "Menu principal",
  },
  PAGE_RECHERCHE: {
    TITRE: "Rechercher une formation ou un métier",
    EXEMPLES_MÉTIERS: "Exemples de métiers accessibles après cette formation",
    FORMATIONS_POUR_APPRENDRE_METIER: "formations pour apprendre le métier",
  },
  ÉLÈVE: {
    PROJET: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mon projet supérieur",
        TITRE: "As-tu déjà un projet d’études supérieures ?",
      },
      SITUATION: {
        LÉGENDE: "Ma situation *",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Je n'ai encore aucune idée",
            DESCRIPTION: "Ca tombe bien, MPS est là pour ça.",
            EMOJI: "😇",
          },
          QUELQUES_PISTES: {
            LABEL: "J’ai déjà quelques pistes d’orientation",
            DESCRIPTION: "Super, MPS va t’aider à affiner ton projet post-bac.",
            EMOJI: "🤔",
          },
          PROJET_PRÉCIS: {
            LABEL: "J'ai déjà un projet précis",
            DESCRIPTION: "Formidable, explorons tes options ensemble.",
            EMOJI: "🧐",
          },
        },
      },
    },
    SCOLARITÉ: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Ma scolarité",
        TITRE: "Dis-nous en plus sur ta scolarité",
      },
      CLASSE: {
        LABEL: "Classe actuelle *",
        OPTIONS: {
          SECONDE: {
            LABEL: "Seconde Générale et Technologique",
          },
          SECONDE_STHR: {
            LABEL: "Seconde STHR",
          },
          SECONDE_TMD: {
            LABEL: "Seconde TMD",
          },
          PREMIÈRE: {
            LABEL: "Première",
          },
          TERMINALE: {
            LABEL: "Terminale",
          },
        },
      },
      BAC: {
        LABEL: "Type de bac choisi ou envisagé",
      },
      SPÉCIALITÉS: {
        LABEL: "Enseignements de spécialité (EDS) choisis ou envisagés",
        DESCRIPTION: "Commence à taper puis sélectionne des enseignements",
        SÉLECTIONNÉS: "Enseignement(s) de spécialité sélectionné(s)",
      },
    },
    DOMAINES: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mes domaines professionnels",
        TITRE: "As-tu déjà identifié des domaines ?",
      },
    },
    INTÊRETS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mes centres d'intêrets",
        TITRE: "Plus tard, je voudrais ...",
      },
    },
    MÉTIERS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Les métiers qui m'inspirent",
        TITRE: "As-tu déjà quelques idées de métiers ?",
      },
      SITUATION: {
        LÉGENDE: "Mon avancement *",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Pas pour l’instant",
            DESCRIPTION: "Ce n'est pas grave, MPS est là pour t'accompagner.",
            EMOJI: "🤔",
          },
          QUELQUES_PISTES: {
            LABEL: "J’ai identifié un ou plusieurs métiers",
            EMOJI: "🙂",
          },
        },
      },
      MÉTIERS_ENVISAGÉS: {
        LABEL: "Métiers envisagés",
        DESCRIPTION: "Commence à taper puis ajoute en favoris les métiers qui pourraient t’intéresser",
        SÉLECTIONNÉS: "Métiers sélectionné(s)",
      },
    },
    ÉTUDES: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mes futures études",
        TITRE: "À propos des études supérieures",
      },
    },
    FORMATIONS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Les études ou cursus",
        TITRE: "Y a-t-il des études ou des cursus qui t'attirent ?",
      },
    },
  },
  PAGE_ACCUEIL: {
    TITLE: pages.ACCUEIL,
  },
  COMMUN: {
    FERMER: "Fermer",
    CHAMPS_OBLIGATOIRES: "Les champs marqués du symbole * sont obligatoires.",
    CONTINUER: "Continuer",
    RETOUR: "Retour",
    FORMATION: "Formation",
    MÉTIER: "Métier",
    TAUX_AFFINITÉ: "Taux d'affinité",
    SÉLECTIONNER_OPTION: "Sélectionner une option",
    PRÉCISER_CATÉGORIES: "N’hésite pas à préciser certaines catégories",
    ERREURS_FORMULAIRES: {
      LISTE_OBLIGATOIRE: "Veuillez sélectionner une option parmi la liste.",
    },
  },
  ACCESSIBILITÉ: {
    LIEN_EXTERNE: "ouvre un lien externe",
    LIEN_EMAIL: "envoyer un email",
    LIEN_TÉLÉPHONE: "composer le numéro",
    RETIRER: "Retirer",
  },
} as const;
