import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type MétiersAccessiblesFicheFormationProps = {
  métiers: FicheFormation["métiersAccessibles"];
};

export type UseMétiersAccessiblesFicheFormationArgs = {
  métiers: MétiersAccessiblesFicheFormationProps["métiers"];
};
