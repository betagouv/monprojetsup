import { type components } from "@/types/api-mps";

export type RéférentielDonnées = {
  élève: {
    situations: SituationÉlève[];
    classes: ClasseÉlève[];
    duréesÉtudesPrévue: DuréeÉtudesPrévueÉlève[];
    alternances: AlternanceÉlève[];
  };
  bacs: Bac[];
  centresIntêrets: CatégorieCentreIntêret[];
  domainesProfessionnels: CatégorieDomainesProfessionnels[];
};

export type SituationÉlève = NonNullable<components["schemas"]["ModificationProfilDTO"]["situation"]>;
export type ClasseÉlève = NonNullable<components["schemas"]["ModificationProfilDTO"]["classe"]>;
export type DuréeÉtudesPrévueÉlève = NonNullable<components["schemas"]["ModificationProfilDTO"]["dureeEtudesPrevue"]>;
export type AlternanceÉlève = NonNullable<components["schemas"]["ModificationProfilDTO"]["alternance"]>;

export type SpécialitéBac = {
  id: string;
  nom: string;
};

export type Bac = {
  id: string;
  nom: string;
  spécialités: SpécialitéBac[];
};

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

export type SousCatégorieDomaineProfessionnel = {
  id: string;
  nom: string;
  emoji: string;
};

export type CatégorieDomainesProfessionnels = {
  id: string;
  nom: string;
  emoji: string;
  sousCatégoriesdomainesProfessionnels: SousCatégorieDomaineProfessionnel[];
};
