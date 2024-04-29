import { type SituationMétiersOptions, type useMétiersFormArgs } from "./MétiersForm.interface";
import { métiersValidationSchema } from "./MétiersForm.validation";
import { type SélecteurMultipleAvecAppelHttpOption } from "@/components/SélecteurMultipleAvecAppelHttp/SélecteurMultipleAvecAppelHttp.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { type MétierAperçu } from "@/features/métier/domain/métier.interface";
import { rechercheMétiersQueryOptions, récupérerAperçusMétiersQueryOptions } from "@/features/métier/ui/options";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès }: useMétiersFormArgs) {
  const [rechercheMétier, setRechercheMétier] = useState<string>();

  const aperçuMétierVersOptionMétier = useCallback((métier: MétierAperçu) => {
    return {
      valeur: métier.id,
      label: métier.nom,
    };
  }, []);

  const { register, erreurs, mettreÀJourÉlève, watch, setValue, getValues } = useÉlèveForm({
    schémaValidation: métiersValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const valeurSituationMétiers = watch("situationMétiers");

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const métiersSélectionnésParDéfaut = useMemo(() => getValues("métiers"), [getValues, valeurSituationMétiers]);

  const {
    data: métiers,
    refetch: rechercherMétiers,
    isFetching: rechercheMétiersEnCours,
  } = useQuery(rechercheMétiersQueryOptions(rechercheMétier));

  const { data: aperçusMétiersSélectionnésParDéfaut } = useQuery(
    récupérerAperçusMétiersQueryOptions(métiersSélectionnésParDéfaut),
  );

  useEffect(() => {
    rechercherMétiers();
  }, [rechercheMétier, rechercherMétiers]);

  useEffect(() => {
    if (valeurSituationMétiers === "aucune_idee") {
      setValue("métiers", []);
    }
  }, [setValue, valeurSituationMétiers]);

  const auChangementDesMétiersSélectionnés = (métiersSélectionnés: SélecteurMultipleAvecAppelHttpOption[]) => {
    setValue(
      "métiers",
      métiersSélectionnés.map((métier) => métier.valeur),
    );
  };

  const situationMétiersOptions: SituationMétiersOptions = [
    {
      valeur: "quelques_pistes",
      label: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      pictogramme: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
    },
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
      description: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
    },
  ];

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    situationMétiersOptions,
    valeurSituationMétiers,
    métiersSuggérés: métiers?.map(aperçuMétierVersOptionMétier) ?? [],
    métiersSélectionnésParDéfaut: aperçusMétiersSélectionnésParDéfaut?.map(aperçuMétierVersOptionMétier),
    rechercheMétiersEnCours,
    auChangementDesMétiersSélectionnés,
    àLaRechercheDUnMétier: setRechercheMétier,
  };
}
