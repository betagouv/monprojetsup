import { type CommuneFavorite } from "@/features/élève/domain/élève.interface";
import { type Formation } from "@/features/formation/domain/formation.interface";

export type ÉtablissementsVoeuxOngletProps = {
  formation: Formation;
  codeCommune: CommuneFavorite["codeInsee"];
};

export type UseÉtablissementsVoeuxOngletArgs = {
  formation: ÉtablissementsVoeuxOngletProps["formation"];
  codeCommune: ÉtablissementsVoeuxOngletProps["codeCommune"];
};
