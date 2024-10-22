export type MétierSansFormationsAssociées = {
  id: string;
  nom: string;
  descriptif: string | null;
  liens: Array<{
    intitulé: string;
    url: string;
  }>;
};

export type Métier = MétierSansFormationsAssociées & {
  formations: Array<{
    id: string;
    nom: string;
  }>;
};
