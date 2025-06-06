import { type z, ZodRawShape } from "zod";

export type UseÉlèveFormArgs = {
  schémaValidation?: z.ZodObject<ZodRawShape>;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
};
