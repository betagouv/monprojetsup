import { type operations } from "@/types/api-mps";

export type RécupérerFichesFormationsRéponseHTTP =
  operations["getFichesFormations"]["responses"]["200"]["content"]["*/*"];

export type RécupérerFormationsRéponseHTTP = operations["getFormations"]["responses"]["200"]["content"]["*/*"];

export type RécupérerSuggestionsFormationsRéponseHTTP =
  operations["getSuggestionsFormations"]["responses"]["200"]["content"]["*/*"];

export type LiensFormationRéponseHTTP =
  RécupérerFichesFormationsRéponseHTTP["formations"][number]["formation"]["liens"];
