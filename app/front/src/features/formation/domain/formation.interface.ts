export type Formation = {
  id: string;
  nom: string;
  descriptifs: {
    formation: string | null;
    détails: string | null;
    attendus: string | null;
    conseils: string | null;
  };
  liens: Array<{
    intitulé: string;
    url: string;
  }>;
  admis: {
    moyenneGénérale: {
      idBac: string | null;
      nomBac: string | null;
      centilles: Array<{
        centille: number;
        note: number;
      }>;
    };
    répartition: {
      parBac: Array<{ idBac: string; nomBac: string; nombre: number; pourcentage: number }>;
    };
    total: number | null;
  };
  formationsAssociées: string[];
  critèresAnalyse: Array<{ nom: string; pourcentage: number }>;
  villes: string[];
  métiersAccessibles: Array<{
    id: string;
    nom: string;
    descriptif: string | null;
    liens: Array<{
      intitulé: string;
      url: string;
    }>;
  }>;
  affinité?: number;
};

export type FormationAperçu = {
  id: Formation["id"];
  nom: Formation["nom"];
};
