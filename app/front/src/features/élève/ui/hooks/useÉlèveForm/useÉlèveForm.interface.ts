import { type z } from "zod";

export type UseÉlèveFormArgs = {
  schémaValidation: z.ZodObject<{}>;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};
