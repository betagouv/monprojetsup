import { type Formation } from "@/features/formation/domain/formation.interface";

export type FormationMasquéeProps = {
  formation: Formation;
};

export type UseFormationMasquéeArgs = {
  formation: FormationMasquéeProps["formation"];
};
