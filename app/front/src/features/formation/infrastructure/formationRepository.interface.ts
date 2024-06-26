import { type Formation, type FormationAperçu } from "@/features/formation/domain/formation.interface";

export type FormationRepository = {
  rechercher: (recherche: string) => Promise<FormationAperçu[] | undefined>;
  récupérerAperçus: (formationIds: Array<FormationAperçu["id"]>) => Promise<FormationAperçu[] | undefined>;
  récupérer: (formationId: Formation["id"]) => Promise<Formation | undefined>;
};
