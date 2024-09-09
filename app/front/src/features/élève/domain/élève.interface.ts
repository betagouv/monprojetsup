import { type Commune } from "@/features/commune/domain/commune.interface";
import {
  type AlternanceÉlève,
  type ClasseÉlève,
  type DuréeÉtudesPrévueÉlève,
  type SituationÉlève,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type components } from "@/types/api-mps";

type BacÉlève = NonNullable<components["schemas"]["ModificationProfilDTO"]["baccalaureat"]>;

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
  formationsFavorites: FormationFavorite[] | null;
  formationsMasquées: string[] | null;
};

type FormationFavorite = {
  id: string;
  niveauAmbition: 1 | 2 | 3 | null;
  tripletsAffectationsChoisis: string[];
  commentaire: string | null;
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
