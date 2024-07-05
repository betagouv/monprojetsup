export type FormationsFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type useFormationsFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export const situationFormationsÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationFormationsÉlève = (typeof situationFormationsÉlève)[number];
