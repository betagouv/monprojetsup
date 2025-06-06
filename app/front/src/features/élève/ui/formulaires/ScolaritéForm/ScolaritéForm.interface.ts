import { type Élève } from "@/features/élève/domain/élève.interface";
import { type RéférentielDonnées } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type UseFormGetValues, type UseFormSetValue, type UseFormWatch } from "react-hook-form";

export type ScolaritéFormProps = {
  formId: string;
  àLaSoumissionDuFormulaireAvecSuccès?: () => Promise<void> | void;
};

export type UseScolaritéFormArgs = {
  àLaSoumissionDuFormulaireAvecSuccès: ScolaritéFormProps["àLaSoumissionDuFormulaireAvecSuccès"];
};

export type UseMoyenneScolaritéFormArgs = {
  référentielDonnées?: RéférentielDonnées | null;
  watch: UseFormWatch<Élève>;
  setValue: UseFormSetValue<Élève>;
  getValues: UseFormGetValues<Élève>;
};
