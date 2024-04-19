import { type projetValidationSchema } from "./ProjetForm.validation";
import { type z } from "zod";

export type ProjetFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type SituationOptions = Array<{
  valeur: z.infer<typeof projetValidationSchema>["situation"];
  label: string;
  description: string;
  pictogramme: string;
}>;
