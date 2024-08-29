import { type Formation } from "@/features/formation/domain/formation.interface";

export type FormationRepository = {
  récupérer: (formationId: Formation["id"]) => Promise<Formation | undefined>;
  récupérerPlusieurs: (formationIds: Array<Formation["id"]>) => Promise<Formation[] | undefined>;
  rechercher: (recherche: string) => Promise<Formation[] | undefined>;
};
