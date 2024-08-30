import { type Bac, type ClasseÉlève } from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export type ScolaritéFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type ClasseOptions = Array<{
  valeur: ClasseÉlève;
  label: string;
}>;

export type BacOptions = Array<{
  valeur: Bac["id"];
  label: string;
}>;

export type useScolaritéFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};
