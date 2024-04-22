import { type ClasseÉlève } from "@/features/élève/domain/élève.interface";

export type ScolaritéFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type ClasseOptions = Array<{
  valeur: ClasseÉlève;
  label: string;
}>;
