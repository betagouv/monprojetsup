export type Métier = {
  id: string;
  nom: string;
  descriptif: string | null;
  liens: Array<{
    intitulé: string;
    url: string;
  }>;
};
