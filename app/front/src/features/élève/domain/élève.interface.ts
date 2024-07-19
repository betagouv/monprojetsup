import { type Commune } from "@/features/commune/domain/commune.interface";

export type Élève = {
  situation: SituationÉlève | null;
  classe: ClasseÉlève | null;
  bac: string | null;
  spécialités: string[] | null;
  domaines: string[] | null;
  centresIntêrets: string[] | null;
  métiersFavoris: string[] | null;
  duréeÉtudesPrévue: DuréeÉtudesPrévueÉlève | null;
  alternance: AlternanceÉlève | null;
  communesFavorites: Commune[] | null;
  formationsFavorites: string[] | null;
};

export const situationÉlève = ["aucune_idee", "quelques_pistes", "projet_precis"] as const;
export type SituationÉlève = (typeof situationÉlève)[number];

export const classeÉlève = ["seconde", "premiere", "terminale"] as const;
export type ClasseÉlève = (typeof classeÉlève)[number];

export const duréeÉtudesPrévueÉlève = ["indifferent", "courte", "longue", "aucune_idee"] as const;
export type DuréeÉtudesPrévueÉlève = (typeof duréeÉtudesPrévueÉlève)[number];

export const alternanceÉlève = ["pas_interesse", "indifferent", "interesse", "tres_interesse"] as const;
export type AlternanceÉlève = (typeof alternanceÉlève)[number];
