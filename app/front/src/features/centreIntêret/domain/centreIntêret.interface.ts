export type CentreIntêret = {
  id: string;
  nom: string;
};

export type CatégorieCentresIntêrets = {
  nom: string;
  emoji: string;
  centresIntêrets: CentreIntêret[];
};
