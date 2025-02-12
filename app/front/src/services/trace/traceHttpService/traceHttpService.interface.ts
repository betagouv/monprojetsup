import { type operations } from "@/types/api-mps";

export type AjoutTraceRéponseHTTP = operations["ajoutTrace"]["responses"]["200"]["content"]["*/*"];

export type BodyAjoutTraceHTTP = operations["ajoutTrace"]["requestBody"]["content"]["application/json"];
