import { type scolaritéValidationSchema } from "./ScolaritéForm.validation";
import { type z } from "zod";

export type ScolaritéFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type ClasseOptions = Array<{
  valeur: z.infer<typeof scolaritéValidationSchema>["classe"];
  label: string;
}>;
