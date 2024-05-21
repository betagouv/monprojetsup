export type Métier = {
  id: string;
  nom: string;
  descriptif: string | null;
  urls: string[];
  formations: Array<{
    id: string;
    nom: string;
  }>;
};

export type MétierAperçu = {
  id: Métier["id"];
  nom: Métier["nom"];
};
