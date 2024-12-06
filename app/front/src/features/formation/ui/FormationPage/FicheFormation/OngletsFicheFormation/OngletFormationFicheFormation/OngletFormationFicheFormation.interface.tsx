import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type OngletFormationFicheFormationProps = {
  texte: string | null;
  liens: FicheFormation["liens"];
};
