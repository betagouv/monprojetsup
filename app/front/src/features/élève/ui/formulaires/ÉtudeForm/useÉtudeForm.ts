/* eslint-disable react-hooks/rules-of-hooks */
import { type AlternanceOptions, type DuréeÉtudesPrévueOptions, type useÉtudeFormArgs } from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { type Commune } from "@/features/commune/domain/commune.interface";
import { rechercheCommunesQueryOptions } from "@/features/commune/ui/communeQueries";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: useÉtudeFormArgs) {
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
    rechercherCommunes();
  }, [rechercheCommune, rechercherCommunes]);

  useEffect(() => setValue("communesFavorites", []), [setValue]);

  const auChangementDesCommunesSélectionnées = (communesSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      "communesFavorites",
      communesSélectionnées.map((commune) => JSON.parse(commune.valeur)),
    );
  };

  const duréeÉtudesPrévueOptions: DuréeÉtudesPrévueOptions = [
    {
      valeur: "indifferent",
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.INDIFFÉRENT.LABEL,
    },
    {
      valeur: "courte",
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.COURTE.LABEL,
    },
    {
      valeur: "longue",
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.LONGUE.LABEL,
    },
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.AUCUNE_IDÉE.LABEL,
    },
  ];

  const alternanceOptions: AlternanceOptions = [
    {
      valeur: "pas_interesse",
      label: i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.PAS_INTÉRESSÉ.LABEL,
    },
    {
      valeur: "indifferent",
      label: i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.INDIFFÉRENT.LABEL,
    },
    {
      valeur: "interesse",
      label: i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.INTÉRESSÉ.LABEL,
    },
    {
      valeur: "tres_interesse",
      label: i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.TRÈS_INTÉRESSÉ.LABEL,
    },
  ];

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
