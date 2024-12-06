export type DomainesFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
  niveauDeTitreCatégories: "h2" | "h3";
};

export type UseDomainesFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès: DomainesFormProps["àLaSoumissionDuFormulaireAvecSuccès"];
};
