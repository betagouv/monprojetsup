import { type projetValidationSchema } from "./ProjetForm.validation";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type z } from "zod";

export type ProjetFormProps = {
  formId: string;
  valeursParDéfaut?: {
    situation?: ProjetFormInputs["situation"];
  };
  àLaSoumissionDuFormulaireSansErreur?: (données: Partial<Omit<Élève, "id">>) => void;
};

export type ProjetFormInputs = z.infer<typeof projetValidationSchema>;

export type SituationOptions = Array<{
  valeur: ProjetFormInputs["situation"];
  label: string;
  description: string;
  pictogramme: string;
}>;
