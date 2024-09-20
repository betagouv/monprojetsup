import { type Élève } from "@/features/élève/domain/élève.interface";
import {
  type Bac,
  type ClasseÉlève,
  type RéférentielDonnées,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type UseFormGetValues, type UseFormSetValue } from "react-hook-form";

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

export type UseSpécialitésFormArgs = {
  référentielDonnées?: RéférentielDonnées | null;
  valeurBac: Élève["bac"] | null;
  setValue: UseFormSetValue<Élève>;
  getValues: UseFormGetValues<Élève>;
};
