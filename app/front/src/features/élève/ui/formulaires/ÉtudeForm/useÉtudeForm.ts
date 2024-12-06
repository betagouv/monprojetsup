/* eslint-disable sonarjs/rules-of-hooks */
import { type UseÉtudeFormArgs } from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import { i18n } from "@/configuration/i18n/i18n";
import {
  AlternanceÉlève,
  DuréeÉtudesPrévueÉlève,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { SelectProps } from "@codegouvfr/react-dsfr/SelectNext";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseÉtudeFormArgs) {
  const { élève } = useÉlève({});
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const { register, erreurs, mettreÀJourÉlève, setValue } = useÉlèveForm({
    schémaValidation: étudeValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const duréeÉtudesPrévueOptions: SelectProps.Option<DuréeÉtudesPrévueÉlève>[] =
    référentielDonnées?.élève.duréesÉtudesPrévue.map((duréeÉtudesPrévue) => ({
      value: duréeÉtudesPrévue,
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS[duréeÉtudesPrévue].LABEL,
    })) ?? [];

  const alternanceOptions: SelectProps.Option<AlternanceÉlève>[] =
    référentielDonnées?.élève.alternances.map((alternance) => ({
      value: alternance,
      label: i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS[alternance].LABEL,
    })) ?? [];

  // Garder synchronisé la valeur react-hook-form et le profil de l'élève
  useEffect(() => {
    setValue("communesFavorites", élève?.communesFavorites ?? []);
  }, [élève?.communesFavorites]);

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
  };
}
