export type useIntérêtsFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type IntérêtsFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
  niveauDeTitreCatégories: "h2" | "h3";
};
