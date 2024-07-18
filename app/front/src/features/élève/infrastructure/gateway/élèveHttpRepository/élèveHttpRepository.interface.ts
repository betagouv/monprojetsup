import { type operations } from "@/types/api-mps";

export type RécupérerProfilÉlèveRéponseHTTP = operations["getProfilEleve"]["responses"]["200"]["content"]["*/*"];
export type MettreÀJourProfilÉlèveRéponseHTTP = operations["postProfilEleve"]["responses"]["200"]["content"]["*/*"];
