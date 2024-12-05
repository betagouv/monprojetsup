import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type FormationMasquéeProps = {
  formation: FicheFormation;
};

export type UseFormationMasquéeArgs = {
  formation: FormationMasquéeProps["formation"];
};
