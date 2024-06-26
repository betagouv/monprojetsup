import { type Formation } from "@/features/formation/domain/formation.interface";

export type MétiersAccessiblesFicheFormationProps = {
  métiers: Formation["métiersAccessibles"];
};

export type ContenuModale = {
  titre: string;
  contenu: string | null;
  liens: Formation["liens"];
};
