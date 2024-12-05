import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type BarreLatéraleFicheFormationProps = {
  suggestions?: FicheFormation[];
  résultatsDeRecherche?: FicheFormation[];
  chargementEnCours: boolean;
};
