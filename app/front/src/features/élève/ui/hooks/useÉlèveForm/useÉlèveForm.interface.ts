import { type z, ZodRawShape } from "zod";

export type UseÉlèveFormArgs = {
  schémaValidation: z.ZodObject<ZodRawShape>;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};
