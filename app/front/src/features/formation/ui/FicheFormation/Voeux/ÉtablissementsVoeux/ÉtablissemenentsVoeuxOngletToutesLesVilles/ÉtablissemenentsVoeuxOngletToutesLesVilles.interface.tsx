import { type Formation } from "@/features/formation/domain/formation.interface";

export type ÉtablissemenentsVoeuxOngletToutesLesVillesProps = {
  formation: Formation;
};

export type UseÉtablissementsVoeuxOngletToutesLesVillesArgs = {
  formation: ÉtablissemenentsVoeuxOngletToutesLesVillesProps["formation"];
};
