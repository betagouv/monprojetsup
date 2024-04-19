import { type Élève } from "@/features/élève/domain/élève.interface";
import { type z } from "zod";

export type UseÉlèveFormArgs = {
  schémaValidation: z.ZodObject<{}>;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type ÉlèveInformationsModifiables = Partial<Omit<Élève, "id">>;
