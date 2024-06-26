import { type router } from "@/configuration/lib/tanstack-router";

export type ÉtapesInscriptionÉlèveStore = {
  étapes: Étape[];
  indexÉtapeActuelle: number | undefined;
  étapeActuelle: Étape | undefined;
  étapeSuivante: Étape | undefined;
  étapePrécédente: Étape | undefined;
  actions: {
    définirÉtapeActuelle: (url: string) => void;
  };
};

type Étape = {
  titreÉtape: string;
  titre: string;
  url: keyof (typeof router)["routesByPath"];
};
