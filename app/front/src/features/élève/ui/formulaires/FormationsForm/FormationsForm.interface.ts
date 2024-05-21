import { type formationsValidationSchema } from "./FormationsForm.validation";
import { type z } from "zod";

export type FormationsFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type SituationFormationsOptions = Array<{
  valeur: z.infer<typeof formationsValidationSchema>["situationFormations"];
  label: string;
  description?: string;
  pictogramme: string;
}>;

export type useFormationsFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};
