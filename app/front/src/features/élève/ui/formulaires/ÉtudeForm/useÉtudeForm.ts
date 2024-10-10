import { type AlternanceOptions, type DuréeÉtudesPrévueOptions, type UseÉtudeFormArgs } from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import useCommunesÉtudeForm from "./useCommunesÉtudeForm";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseÉtudeFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const { register, erreurs, mettreÀJourÉlève, getValues, setValue } = useÉlèveForm({
    schémaValidation: étudeValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const duréeÉtudesPrévueOptions: DuréeÉtudesPrévueOptions =
    référentielDonnées?.élève.duréesÉtudesPrévue.map((duréeÉtudesPrévue) => ({
      valeur: duréeÉtudesPrévue,
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS[duréeÉtudesPrévue].LABEL,
    })) ?? [];

  const alternanceOptions: AlternanceOptions =
    référentielDonnées?.élève.alternances.map((alternance) => ({
      valeur: alternance,
      label: i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS[alternance].LABEL,
    })) ?? [];

  const communesÉtudeForm = useCommunesÉtudeForm({
    setValue,
    getValues,
  });

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
    ...communesÉtudeForm,
  };
}
