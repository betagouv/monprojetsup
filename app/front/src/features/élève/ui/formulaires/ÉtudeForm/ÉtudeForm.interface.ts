export type ÉtudeFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
};

export type UseÉtudeFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès: ÉtudeFormProps["àLaSoumissionDuFormulaireAvecSuccès"];
};
