export type TraceService = {
  ajouterTraceFicheFormation: (id: string) => Promise<void>;
  ajouterTraceFicheMetier: (id: string) => Promise<void>;
  ajouterTraceRechercheFormation: (mots: string) => Promise<void>;
  ajouterTraceSuggestions: () => Promise<void>;
  ajouterTraceOngletFicheFormation: (id: string, idOnglet: string) => Promise<void>;
  ajouterTraceLienExterne: (id: string, url: string) => Promise<void>;
};
