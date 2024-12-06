import { Élève } from "@/features/élève/domain/élève.interface";
import { RadioButtonsProps } from "@codegouvfr/react-dsfr/RadioButtons";
import { UseFormGetValues, UseFormSetValue } from "react-hook-form";

export const situationMétiersÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationMétiersÉlève = (typeof situationMétiersÉlève)[number];

export type MétiersFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
};

export type UseMétiersFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès: MétiersFormProps["àLaSoumissionDuFormulaireAvecSuccès"];
};

export type UseMétiersFavorisMétiersFormArgs = {
  setValue: UseFormSetValue<Élève>;
  getValues: UseFormGetValues<Élève>;
};

export type UseSituationMétiersMétiersFormArgs = {
  setValue: UseFormSetValue<Élève>;
  getValues: UseFormGetValues<Élève>;
};

export type StatusSituationMétiers = {
  type: RadioButtonsProps["state"];
  message: RadioButtonsProps["stateRelatedMessage"];
};
