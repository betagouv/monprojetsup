/* eslint-disable react-hooks/rules-of-hooks */
import {
  type AlternanceOptions,
  type DuréeÉtudesPrévueOptions,
  type SituationVillesOptions,
  type useÉtudeFormArgs,
} from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { type Ville } from "@/features/ville/domain/ville.interface";
import { rechercheVillesQueryOptions } from "@/features/ville/ui/options";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: useÉtudeFormArgs) {
  const [rechercheVille, setRechercheVille] = useState<string>();

  const villeVersOptionVille = useCallback((ville: Ville) => {
    return {
      valeur: JSON.stringify(ville),
      label: ville.nom,
    };
  }, []);

  const { register, erreurs, mettreÀJourÉlève, getValues, setValue, watch } = useÉlèveForm({
    schémaValidation: étudeValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const valeurSituationVilles = watch("situationVilles");

  const villesSélectionnéesParDéfaut = useMemo(
    () => getValues("villes"),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [getValues, valeurSituationVilles],
  );

  const {
    data: villes,
    refetch: rechercherVilles,
    isFetching: rechercheVillesEnCours,
  } = useQuery(rechercheVillesQueryOptions(rechercheVille));

  useEffect(() => {
    rechercherVilles();
  }, [rechercheVille, rechercherVilles]);

  useEffect(() => {
    if (valeurSituationVilles === "aucune_idee") {
      setValue("villes", []);
    }
  }, [setValue, valeurSituationVilles]);

  const auChangementDesVillesSélectionnées = (villesSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      "villes",
      villesSélectionnées.map((ville) => JSON.parse(ville.valeur)),
    );
  };

  const duréeÉtudesPrévueOptions: DuréeÉtudesPrévueOptions = [
    {
      valeur: "options_ouvertes",
      label: i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.OPTIONS_OUVERTES.LABEL,
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

  const situationVillesOptions: SituationVillesOptions = [
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.ÉTUDE.SITUATION_VILLES.OPTIONS.AUCUNE_IDÉE.LABEL,
    },
    {
      valeur: "quelques_pistes",
      label: i18n.ÉLÈVE.ÉTUDE.SITUATION_VILLES.OPTIONS.QUELQUES_PISTES.LABEL,
    },
  ];

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
    situationVillesOptions,
    valeurSituationVilles,
    villesSuggérées: villes?.map(villeVersOptionVille) ?? [],
    villesSélectionnéesParDéfaut: villesSélectionnéesParDéfaut?.map(villeVersOptionVille) ?? [],
    rechercheVillesEnCours,
    auChangementDesVillesSélectionnées,
    àLaRechercheDUneVille: setRechercheVille,
  };
}
