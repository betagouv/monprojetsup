import { type components } from "@/types/api-mps";

export type RéférentielDonnées = {
  élève: {
    situations: SituationÉlève[];
    classes: ClasseÉlève[];
    duréesÉtudesPrévue: DuréeÉtudesPrévueÉlève[];
    alternances: AlternanceÉlève[];
  };
  bacs: Bac[];
  centresIntérêts: CatégorieCentreIntérêt[];
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
  statistiquesAdmission: {
    parMoyenneGénérale: Array<{
      moyenne: number;
      pourcentageAdmisAyantCetteMoyenneOuMoins: number;
    }> | null;
  };
};

export type SousCatégorieCentreIntérêt = {
  id: string;
  nom: string;
  emoji: string;
};

export type CatégorieCentreIntérêt = {
  id: string;
  nom: string;
  emoji: string;
  sousCatégoriesCentreIntérêt: SousCatégorieCentreIntérêt[];
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
