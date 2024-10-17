import { type Formation } from "@/features/formation/domain/formation.interface";

export type NombreVillesDontFavoritesProps = {
  communesProposantLaFormation: Formation["communesProposantLaFormation"];
  explications?: Formation["explications"];
};
