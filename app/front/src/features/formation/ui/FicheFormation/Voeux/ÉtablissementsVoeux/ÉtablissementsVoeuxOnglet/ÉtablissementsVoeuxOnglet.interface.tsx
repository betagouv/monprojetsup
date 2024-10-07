import { type Formation } from "@/features/formation/domain/formation.interface";

export type ÉtablissementsVoeuxOngletProps = {
  établissements: Formation["établissementsParCommuneFavorites"][number]["établissements"];
  formationId: Formation["id"];
};

export type useÉtablissementsVoeuxOngletArgs = {
  établissements: ÉtablissementsVoeuxOngletProps["établissements"];
  formationId: ÉtablissementsVoeuxOngletProps["formationId"];
};
