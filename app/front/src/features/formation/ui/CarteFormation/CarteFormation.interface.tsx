import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type CarteFormationProps = {
  id: FicheFormation["id"];
  titre: FicheFormation["nom"];
  métiersAccessibles: FicheFormation["métiersAccessibles"];
  communes: FicheFormation["communesProposantLaFormation"];
  affinité?: FicheFormation["affinité"];
  sélectionnée?: boolean;
};
