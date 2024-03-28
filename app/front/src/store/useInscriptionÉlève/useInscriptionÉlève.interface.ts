export type InscriptionÉlèveStore = {
  étapes: ÉtapeInscriptionÉlève[];
  indexÉtapeActuelle: number | undefined;
  étapeActuelle: ÉtapeInscriptionÉlève | undefined;
  étapeSuivante: ÉtapeInscriptionÉlève | undefined;
  étapePrécédente: ÉtapeInscriptionÉlève | undefined;
  actions: {
    définirÉtapeActuelle: (url: string) => void;
  };
};

export type ÉtapeInscriptionÉlève = {
  titreÉtape: string;
  titre: string;
  url: string;
};
