import { type Formation } from "@/features/formation/domain/formation.interface";

export type ÉtablissemenentsVoeuxOngletToutesLesVillesProps = {
  établissements: Formation["établissements"];
  formationId: Formation["id"];
};
