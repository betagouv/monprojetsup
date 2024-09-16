import { type Formation } from "@/features/formation/domain/formation.interface";

export type BarreLatéraleFicheFormationProps = {
  recherche?: string;
  suggestions?: Formation[];
  résultatsDeRecherche?: Formation[];
};
