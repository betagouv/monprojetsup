import { type métiersValidationSchema } from "./MétiersForm.validation";
import { type z } from "zod";

export type MétiersFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type SituationMétiersOptions = Array<{
  valeur: z.infer<typeof métiersValidationSchema>["situationMétiers"];
  label: string;
  description?: string;
  pictogramme: string;
}>;

export type useMétiersFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};
