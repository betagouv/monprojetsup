/* eslint-disable react-hooks/rules-of-hooks */
/* eslint-disable sonarjs/pluginRules-of-hooks */
import { type AlternanceOptions, type DuréeÉtudesPrévueOptions, type useÉtudeFormArgs } from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { type Commune } from "@/features/commune/domain/commune.interface";
import { rechercheCommunesQueryOptions } from "@/features/commune/ui/communeQueries";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: useÉtudeFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const [rechercheCommune, setRechercheCommune] = useState<string>();

  const communeVersOptionCommune = useCallback((commune: Commune) => {
    return {
      valeur: JSON.stringify(commune),
      label: commune.nom,
    };
  }, []);

  const { register, erreurs, mettreÀJourÉlève, getValues, setValue } = useÉlèveForm({
    schémaValidation: étudeValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const communesSélectionnéesParDéfaut = useMemo(() => getValues("communesFavorites"), [getValues]);

  const {
    data: communes,
    refetch: rechercherCommunes,
    isFetching: rechercheCommunesEnCours,
  } = useQuery(rechercheCommunesQueryOptions(rechercheCommune));

  useEffect(() => {
    if (rechercheCommune && rechercheCommune.length >= 3) {
      rechercherCommunes();
    }
  }, [rechercheCommune, rechercherCommunes]);

  useEffect(() => setValue("communesFavorites", []), [setValue]);

  const auChangementDesCommunesSélectionnées = (communesSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      "communesFavorites",
      communesSélectionnées.map((commune) => JSON.parse(commune.valeur)),
    );
  };

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

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
    communesSuggérées: communes?.map(communeVersOptionCommune) ?? [],
    communesSélectionnéesParDéfaut: communesSélectionnéesParDéfaut?.map(communeVersOptionCommune) ?? [],
    rechercheCommunesEnCours,
    auChangementDesCommunesSélectionnées,
    àLaRechercheDUneCommune: setRechercheCommune,
  };
}
