import { type Ville } from "@/features/ville/domain/ville.interface";

export type Élève = {
  id: string;
  nom: string;
  prénom: string;
  nomUtilisateur: string;
  email?: string;
  situation?: SituationÉlève;
  classe?: ClasseÉlève;
  bac?: string;
  spécialités?: string[];
  domaines?: string[];
  centresIntêrets?: string[];
  métiers?: string[];
  duréeÉtudesPrévue?: DuréeÉtudesPrévueÉlève;
  alternance?: AlternanceÉlève;
  villes?: Ville[];
  formations?: string[];
};

export const situationÉlève = ["aucune_idee", "quelques_pistes", "projet_precis"] as const;
export type SituationÉlève = (typeof situationÉlève)[number];

export const classeÉlève = ["seconde", "premiere", "terminale"] as const;
export type ClasseÉlève = (typeof classeÉlève)[number];

export const duréeÉtudesPrévueÉlève = ["indifferent", "courte", "longue", "aucune_idee"] as const;
export type DuréeÉtudesPrévueÉlève = (typeof duréeÉtudesPrévueÉlève)[number];

export const alternanceÉlève = ["pas_interesse", "indifferent", "interesse", "tres_interesse"] as const;
export type AlternanceÉlève = (typeof alternanceÉlève)[number];
