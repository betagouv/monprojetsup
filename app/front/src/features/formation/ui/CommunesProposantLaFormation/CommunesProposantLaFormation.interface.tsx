import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type CommunesProposantLaFormationProps = {
  communes: FicheFormation["communesProposantLaFormation"];
  lienParcoursSup?: FicheFormation["lienParcoursSup"];
};
