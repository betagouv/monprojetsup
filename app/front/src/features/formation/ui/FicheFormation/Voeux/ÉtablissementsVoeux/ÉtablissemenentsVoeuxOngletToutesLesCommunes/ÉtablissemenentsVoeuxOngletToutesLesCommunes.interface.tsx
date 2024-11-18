import { type Formation } from "@/features/formation/domain/formation.interface";

export type ÉtablissemenentsVoeuxOngletToutesLesCommunesProps = {
  formation: Formation;
};

export type UseÉtablissementsVoeuxOngletToutesLesCommunesArgs = {
  formation: ÉtablissemenentsVoeuxOngletToutesLesCommunesProps["formation"];
};
