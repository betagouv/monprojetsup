import { type Formation } from "@/features/formation/domain/formation.interface";

export type VoeuxOngletToutesLesCommunesProps = {
  formation: Formation;
};

export type UseVoeuxOngletToutesLesCommunesArgs = {
  formation: VoeuxOngletToutesLesCommunesProps["formation"];
};
