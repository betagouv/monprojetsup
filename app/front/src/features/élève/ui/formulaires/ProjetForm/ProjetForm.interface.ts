import { type projetValidationSchema } from "./ProjetForm.validation";
import { type z } from "zod";

export type ProjetFormProps = {
  formId: string;
  valeursParDéfaut?: {
    situation?: ProjetFormInputs["situation"];
  };
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type ProjetFormInputs = z.infer<typeof projetValidationSchema>;

export type SituationOptions = Array<{
  valeur: ProjetFormInputs["situation"];
  label: string;
  description: string;
  pictogramme: string;
}>;
