import { type Formation } from "@/features/formation/domain/formation.interface";

export type BarreLatéraleFicheFormationProps = {
  suggestions?: Formation[];
  résultatsDeRecherche?: Formation[];
  chargementEnCours: boolean;
};
