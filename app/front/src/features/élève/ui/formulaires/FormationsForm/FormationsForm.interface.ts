import { Élève } from "@/features/élève/domain/élève.interface";
import { RadioButtonsProps } from "@codegouvfr/react-dsfr/RadioButtons";
import { UseFormGetValues, UseFormSetValue } from "react-hook-form";

export const situationFormationsÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationFormationsÉlève = (typeof situationFormationsÉlève)[number];

export type FormationsFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
};

export type UseFormationsFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: FormationsFormProps["àLaSoumissionDuFormulaireAvecSuccès"];
};

export type UseSituationFormationsFormationsFormArgs = {
  setValue: UseFormSetValue<Élève>;
  getValues: UseFormGetValues<Élève>;
};

export type StatusSituationFormations = {
  type: RadioButtonsProps["state"];
  message: RadioButtonsProps["stateRelatedMessage"];
};
