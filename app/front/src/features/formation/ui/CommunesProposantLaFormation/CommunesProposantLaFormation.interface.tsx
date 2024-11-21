import { type Formation } from "@/features/formation/domain/formation.interface";

export type CommunesProposantLaFormationProps = {
  communes: Formation["communesProposantLaFormation"];
  lienParcoursSup?: Formation["lienParcoursSup"];
};
