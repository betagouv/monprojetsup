/* eslint-disable sonarjs/redundant-type-aliases */

import { type Commune } from "@/features/commune/domain/commune.interface";
import {
  type AlternanceÉlève,
  BacÉlève,
  type ClasseÉlève,
  type DuréeÉtudesPrévueÉlève,
  type SituationÉlève,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";

type Id = string;

export type MétierÉlève = Id;
export type SpécialitéÉlève = Id;
export type DomaineÉlève = Id;
export type CentreIntêretÉlève = Id;
export type FormationMasquéeÉlève = Id;
export type CommuneÉlève = Omit<Commune, "codePostal">;
export type FormationÉlève = Id;
export type VoeuÉlève = {
  id: Id;
  estParcoursup: boolean;
};
export type NotePersonnelleFormationÉlève = {
  idFormation: Id;
  note: string | null;
};

export type AmbitionFormationÉlève = {
  idFormation: Id;
  ambition: 1 | 2 | 3 | null;
};

export type Élève = {
  compteParcoursupAssocié: boolean;
  situation: SituationÉlève | null;
  classe: ClasseÉlève | null;
  bac: BacÉlève | null;
  spécialités: SpécialitéÉlève[] | null;
  domaines: DomaineÉlève[] | null;
  centresIntérêts: CentreIntêretÉlève[] | null;
  métiersFavoris: MétierÉlève[] | null;
  duréeÉtudesPrévue: DuréeÉtudesPrévueÉlève | null;
  alternance: AlternanceÉlève | null;
  moyenneGénérale: number | null;
  communesFavorites: CommuneÉlève[] | null;
  formations: FormationÉlève[] | null;
  voeuxFavoris: VoeuÉlève[] | null;
  formationsMasquées: FormationMasquéeÉlève[] | null;
  notesPersonnelles: NotePersonnelleFormationÉlève[] | null;
  ambitions: AmbitionFormationÉlève[] | null;
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
