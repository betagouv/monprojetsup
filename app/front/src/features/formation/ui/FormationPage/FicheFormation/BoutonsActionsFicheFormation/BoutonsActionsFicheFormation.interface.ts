import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type useBoutonsActionsFicheFormationArgs = {
  formation: FicheFormation;
  baseOuverte: boolean;
};

export type BoutonsActionsFicheFormationProps = {
  formation: FicheFormation;
  baseOuverte: boolean;
};
