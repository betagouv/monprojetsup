import { type monProjetValidationSchema } from "./MonProjetForm.validation";
import { type z } from "zod";

export type MonProjetFormProps = {
  formId: string;
  valeursParDéfaut?: {
    maSituation: MonProjetFormInputs["maSituation"];
  };
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type MonProjetFormInputs = z.infer<typeof monProjetValidationSchema>;
export type MaSituationOptions = Array<{
  valeur: MonProjetFormInputs["maSituation"];
  label: string;
  description: string;
  pictogramme: string;
}>;
