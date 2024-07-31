import { type Commune } from "@/features/commune/domain/commune.interface";
import { type components } from "@/types/api-mps";

export type SituationÉlève = NonNullable<components["schemas"]["ProfilDTO"]["situation"]>;
export type ClasseÉlève = NonNullable<components["schemas"]["ProfilDTO"]["classe"]>;
export type DuréeÉtudesPrévueÉlève = NonNullable<components["schemas"]["ProfilDTO"]["dureeEtudesPrevue"]>;
export type AlternanceÉlève = NonNullable<components["schemas"]["ProfilDTO"]["alternance"]>;
type BacÉlève = NonNullable<components["schemas"]["ProfilDTO"]["baccalaureat"]>;

export type Élève = {
  situation: SituationÉlève | null;
  classe: ClasseÉlève | null;
  bac: BacÉlève | null;
  spécialités: string[] | null;
  domaines: string[] | null;
  centresIntêrets: string[] | null;
  métiersFavoris: string[] | null;
  duréeÉtudesPrévue: DuréeÉtudesPrévueÉlève | null;
  alternance: AlternanceÉlève | null;
  communesFavorites: Commune[] | null;
  formationsFavorites: string[] | null;
};

export const situationÉlève = [
  "aucune_idee",
  "quelques_pistes",
  "projet_precis",
] as const satisfies readonly SituationÉlève[];

export const classeÉlève = ["seconde", "premiere", "terminale"] as const satisfies readonly ClasseÉlève[];

export const duréeÉtudesPrévueÉlève = [
  "indifferent",
  "courte",
  "longue",
  "aucune_idee",
] as const satisfies readonly DuréeÉtudesPrévueÉlève[];

export const alternanceÉlève = [
  "pas_interesse",
  "indifferent",
  "interesse",
  "tres_interesse",
] as const satisfies readonly AlternanceÉlève[];
