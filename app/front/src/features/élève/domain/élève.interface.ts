import { type Commune } from "@/features/commune/domain/commune.interface";
import {
  type AlternanceÉlève,
  BacÉlève,
  type ClasseÉlève,
  type DuréeÉtudesPrévueÉlève,
  type SituationÉlève,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export type CommuneFavorite = Omit<Commune, "codePostal">;

export type FormationFavorite = {
  id: string;
  niveauAmbition: 1 | 2 | 3 | null;
  commentaire: string | null;
};

export type VoeuFavori = {
  id: string;
  estParcoursup: boolean;
};

export type Élève = {
  compteParcoursupAssocié: boolean;
  situation: SituationÉlève | null;
  classe: ClasseÉlève | null;
  bac: BacÉlève | null;
  spécialités: string[] | null;
  domaines: string[] | null;
  centresIntérêts: string[] | null;
  métiersFavoris: string[] | null;
  duréeÉtudesPrévue: DuréeÉtudesPrévueÉlève | null;
  alternance: AlternanceÉlève | null;
  moyenneGénérale: number | null;
  communesFavorites: CommuneFavorite[] | null;
  formationsFavorites: FormationFavorite[] | null;
  voeuxFavoris: VoeuFavori[] | null;
  formationsMasquées: string[] | null;
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
