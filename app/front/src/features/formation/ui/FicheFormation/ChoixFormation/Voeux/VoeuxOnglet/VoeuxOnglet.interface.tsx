import { type Formation } from "@/features/formation/domain/formation.interface";
import { type CommuneFavorite } from "@/features/élève/domain/élève.interface";

export type VoeuxOngletProps = {
  formation: Formation;
  codeCommune: CommuneFavorite["codeInsee"];
};

export type UseVoeuxOngletArgs = {
  formation: VoeuxOngletProps["formation"];
  codeCommune: VoeuxOngletProps["codeCommune"];
};
