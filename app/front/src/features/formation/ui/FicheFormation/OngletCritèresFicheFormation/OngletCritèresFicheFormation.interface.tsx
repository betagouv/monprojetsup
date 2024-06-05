import { type Formation } from "@/features/formation/domain/formation.interface";

export type OngletCritèresFicheFormationProps = {
  critèresAnalyse: Formation["critèresAnalyse"];
  moyenneGénérale: Formation["admis"]["moyenneGénérale"];
  répartitionParBac: Formation["admis"]["répartition"]["parBac"];
  descriptifAttendus: string | null;
};
