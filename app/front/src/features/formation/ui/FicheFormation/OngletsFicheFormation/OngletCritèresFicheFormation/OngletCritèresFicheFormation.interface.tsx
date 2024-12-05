import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type OngletCritèresFicheFormationProps = {
  critèresAnalyse: FicheFormation["critèresAnalyse"];
  moyenneGénérale: FicheFormation["admis"]["moyenneGénérale"];
  répartitionParBac: FicheFormation["admis"]["répartition"]["parBac"];
  descriptifAttendus: string | null;
};
