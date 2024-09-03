import { type Formation } from "@/features/formation/domain/formation.interface";

export type CarteFormationProps = {
  nom: Formation["nom"];
  métiersAccessibles: Formation["métiersAccessibles"];
  communes: Formation["communes"];
  affinité?: Formation["affinité"];
  sélectionnée?: boolean;
};
