import { Paths } from "@/types/commons";

type Étape = {
  titreÉtape: string;
  titre: string;
  url: Paths;
};

export type InscriptionÉlèveStore = {
  étapes: Étape[];
  indexÉtapeActuelle: number;
  étapeActuelle: Étape | undefined;
  étapeSuivante: Étape | undefined;
  étapePrécédente: Étape | undefined;
  actions: {
    définirÉtapeActuelle: (url: string) => void;
  };
};
