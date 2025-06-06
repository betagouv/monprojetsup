import { type FicheFormation, Formation } from "@/features/formation/domain/formation.interface";

export type FormationRepository = {
  récupérerUneFiche: (formationId: FicheFormation["id"]) => Promise<FicheFormation | Error>;
  récupérerPlusieursFiches: (formationIds: Array<FicheFormation["id"]>) => Promise<FicheFormation[] | Error>;
  récupérerPlusieurs: (formationIds: Array<Formation["id"]>) => Promise<Formation[] | Error>;
  rechercherFichesFormations: (recherche: string) => Promise<FicheFormation[] | Error>;
  rechercherFormations: (recherche: string) => Promise<Formation[] | Error>;
  suggérer: () => Promise<FicheFormation[] | Error>;
};
