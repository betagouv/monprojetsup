export type AnalyticsRepository = {
  estInitialisé: () => boolean;
  envoyerPageVue: (url: string) => void;
  envoyerÉvènement: (catégorie: string, action: string, nom: string) => void;
  estOptOut: () => Promise<boolean> | boolean;
  changerConsentementMatomo: () => Promise<void> | void;
};
