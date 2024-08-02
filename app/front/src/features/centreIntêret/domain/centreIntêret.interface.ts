export type SousCatégorieCentreIntêret = {
  id: string;
  nom: string;
  emoji: string;
};

export type CatégorieCentreIntêret = {
  id: string;
  nom: string;
  emoji: string;
  sousCatégoriesCentreIntêret: SousCatégorieCentreIntêret[];
};
