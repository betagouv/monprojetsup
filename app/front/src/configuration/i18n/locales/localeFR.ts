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
        ERREUR_FORMULAIRE: "Veuillez sélectionner une option parmi la liste.",
      },
    },
    SCOLARITÉ: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Ma scolarité",
        TITRE: "Dis-nous en plus sur ta scolarité",
      },
    },
    ASPIRATIONS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mes aspirations professionnelles",
        TITRE: "As-tu déjà identifié des secteurs d’activité ?",
      },
    },
    TALENTS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mes petits talents",
        TITRE: "Parlons un peu de toi. Tu dirais que tu es ...",
      },
    },
    MÉTIERS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Les métiers qui m'inspirent",
        TITRE: "As-tu déjà quelques idées de métiers ?",
      },
    },
    ÉTUDES: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Mes futures études",
        TITRE: "À propos des études supérieures",
      },
    },
  },
  PAGE_ACCUEIL: {
    TITLE: pages.ACCUEIL,
  },
  PAGE_ARTICLES: {
    TITLE: pages.ARTICLES,
  },
  COMMUN: {
    FERMER: "Fermer",
    CHAMPS_OBLIGATOIRES: "Les champs marqués du symbole * sont obligatoires.",
    CONTINUER: "Continuer",
    RETOUR: "Retour",
  },
  ACCESSIBILITÉ: {
    LIEN_EXTERNE: "ouvre un lien externe",
    LIEN_EMAIL: "envoyer un email",
    LIEN_TÉLÉPHONE: "composer le numéro",
    RETIRER: "Retirer",
  },
} as const;
