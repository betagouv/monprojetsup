import { type Formation } from "@/features/formation/domain/formation.interface";

export type CarteFormationProps = {
  id: Formation["id"];
  titre: Formation["nom"];
  métiersAccessibles: Formation["métiersAccessibles"];
  communes: Formation["communesProposantLaFormation"];
  affinité?: Formation["affinité"];
  sélectionnée?: boolean;
};
