import { type Formation } from "@/features/formation/domain/formation.interface";

export type ExplicationsCorrespondanceFicheFormationProps = {
  explications: NonNullable<Formation["explications"]>;
};
