import { type Formation } from "@/features/formation/domain/formation.interface";

export type CarteFormationProps = {
  id: Formation["id"];
  nom: Formation["nom"];
  métiersAccessibles: Formation["métiersAccessibles"];
  affinité?: Formation["affinité"];
  sélectionnée?: boolean;
};
