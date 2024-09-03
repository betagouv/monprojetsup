import { type operations } from "@/types/api-mps";

export type RécupérerFormationsRéponseHTTP = operations["getFormations"]["responses"]["200"]["content"]["*/*"];

export type RécupérerSuggestionsFormationsRéponseHTTP =
  operations["getSuggestionsFormations"]["responses"]["200"]["content"]["*/*"];
