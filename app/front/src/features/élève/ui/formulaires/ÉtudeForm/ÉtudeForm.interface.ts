import {
  type AlternanceÉlève,
  type DuréeÉtudesPrévueÉlève,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type UseFormGetValues, type UseFormSetValue } from "react-hook-form";

export type ÉtudeFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type DuréeÉtudesPrévueOptions = Array<{
  valeur: DuréeÉtudesPrévueÉlève;
  label: string;
}>;

export type AlternanceOptions = Array<{
  valeur: AlternanceÉlève;
  label: string;
}>;

export type UseÉtudeFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès?: () => void;
};

export type UseCommunesÉtudeFormArgs = {
  setValue: UseFormSetValue<Élève>;
  getValues: UseFormGetValues<Élève>;
};
