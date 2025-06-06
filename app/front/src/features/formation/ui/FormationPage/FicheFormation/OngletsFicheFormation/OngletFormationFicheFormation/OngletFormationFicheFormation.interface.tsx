import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type OngletFormationFicheFormationProps = {
  id: string;
  texte: string | null;
  liens: FicheFormation["liens"];
};
