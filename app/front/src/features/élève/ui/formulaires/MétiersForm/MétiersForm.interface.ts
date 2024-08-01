export type MétiersFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type useMétiersFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export const situationMétiersÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationMétiersÉlève = (typeof situationMétiersÉlève)[number];
