import { type operations } from "@/types/api-mps";

export type RécupérerMétiersRéponseHTTP = operations["getMetiers"]["responses"]["200"]["content"]["*/*"];
export type RechercheSuccincteMétiersRéponseHTTP =
  operations["getRechercheMetierSuccincte"]["responses"]["200"]["content"]["*/*"];
