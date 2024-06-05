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
  situationMétiers?: SituationMétiersÉlève;
  métiers?: string[];
  duréeÉtudesPrévue?: DuréeÉtudesPrévueÉlève;
  alternance?: AlternanceÉlève;
  situationVilles?: SituationVillesÉlève;
  villes?: Ville[];
  situationFormations?: SituationFormationsÉlève;
  formations?: string[];
};

export const situationÉlève = ["aucune_idee", "quelques_pistes", "projet_precis"] as const;
export type SituationÉlève = (typeof situationÉlève)[number];

export const situationMétiersÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationMétiersÉlève = (typeof situationMétiersÉlève)[number];

export const classeÉlève = ["seconde", "seconde_sthr", "seconde_tmd", "premiere", "terminale"] as const;
export type ClasseÉlève = (typeof classeÉlève)[number];

export const duréeÉtudesPrévueÉlève = ["options_ouvertes", "courte", "longue", "aucune_idee"] as const;
export type DuréeÉtudesPrévueÉlève = (typeof duréeÉtudesPrévueÉlève)[number];

export const alternanceÉlève = ["pas_interesse", "indifferent", "interesse", "tres_interesse"] as const;
export type AlternanceÉlève = (typeof alternanceÉlève)[number];

export const situationVillesÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationVillesÉlève = (typeof situationVillesÉlève)[number];

export const situationFormationsÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationFormationsÉlève = (typeof situationFormationsÉlève)[number];
