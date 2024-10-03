import { type Formation } from "@/features/formation/domain/formation.interface";

export type ÉtablissementsVoeuxOngletProps = {
  établissements: Formation["établissementsParCommuneFavorites"][number]["établissements"];
};
