const pages = {
  ACCUEIL: "Accueil",
  ARTICLES: "Articles",
} as const;

export const localeFR = {
  APP: {
    NAME: "Mon Projet Sup",
  },
  NAVIGATION: {
    ACCUEIL: pages.ACCUEIL,
    ARTICLES: pages.ARTICLES,
    MAIN_NAVIGATION: "Menu principal",
  },
  PAGE_ACCUEIL: {
    TITLE: pages.ACCUEIL,
  },
  PAGE_ARTICLES: {
    TITLE: pages.ARTICLES,
  },
  GENERIC: {
    CLOSE: "Fermer",
  },
} as const;
