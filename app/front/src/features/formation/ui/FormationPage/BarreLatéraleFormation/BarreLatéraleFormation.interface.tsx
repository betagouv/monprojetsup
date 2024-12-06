import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type BarreLatéraleFormationProps = {
  suggestions?: FicheFormation[];
  résultatsDeRecherche?: FicheFormation[];
  chargementEnCours: boolean;
};
