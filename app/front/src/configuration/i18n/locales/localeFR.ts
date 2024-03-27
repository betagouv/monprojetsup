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
  INSCRIPTION: {
    ÉTAPES: {
      MON_PROJET: "Mon projet supérieur",
      MA_SCOLARITÉ: "Ma scolarité",
      MES_ASPIRATIONS: "Mes aspirations professionnelles",
      MES_TALENTS: "Mes petits talents",
      MES_MÉTIERS: "Les métiers qui m'inspirent",
      MES_ÉTUDES: "Mes futures études",
    },
    CONTINUER: "Continuer",
    RETOUR: "Retour",
    TITRES: {
      MON_PROJET: "As-tu déjà un projet d’études supérieures ?",
      MA_SCOLARITÉ: "Dis-nous en plus sur ta scolarité",
      MES_ASPIRATIONS: "As-tu déjà identifié des secteurs d’activité ?",
      MES_TALENTS: "Parlons un peu de toi. Tu dirais que tu es ...",
      MES_MÉTIERS: "As-tu déjà quelques idées de métiers ?",
      MES_ÉTUDES: "À propos des études supérieures",
    },
  },
  MON_PROJET: {
    MA_SITUATION: {
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
  PAGE_ACCUEIL: {
    TITLE: pages.ACCUEIL,
  },
  PAGE_ARTICLES: {
    TITLE: pages.ARTICLES,
  },
  COMMUN: {
    CLOSE: "Fermer",
    CHAMPS_OBLIGATOIRES: "Les champs marqués du symbole * sont obligatoires.",
  },
} as const;
