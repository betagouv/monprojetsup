export type IntérêtsFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
  niveauDeTitreCatégories: "h2" | "h3";
};

export type UseIntérêtsFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès: IntérêtsFormProps["àLaSoumissionDuFormulaireAvecSuccès"];
};
