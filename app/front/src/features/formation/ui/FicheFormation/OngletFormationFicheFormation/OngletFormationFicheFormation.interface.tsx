import { type Formation } from "@/features/formation/domain/formation.interface";

export type OngletFormationFicheFormationProps = {
  texte: string | null;
  liens: Formation["liens"];
};
