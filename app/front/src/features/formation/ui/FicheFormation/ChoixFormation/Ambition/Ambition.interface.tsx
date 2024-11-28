import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";

export type AmbitionProps = {
  ambitionActuelle: FormationFavorite["niveauAmbition"] | undefined;
  formationId: Formation["id"];
};

export type useAmbitionArgs = {
  formationId: Formation["id"];
};
