import { type SituationMétiersÉlève, type useMétiersFormArgs } from "./MétiersForm.interface";
import { métiersValidationSchema } from "./MétiersForm.validation";
import { type BoutonRadioRicheProps } from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche.interface";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { type MétierAperçu } from "@/features/métier/domain/métier.interface";
import { rechercheMétiersQueryOptions, récupérerAperçusMétiersQueryOptions } from "@/features/métier/ui/options";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès }: useMétiersFormArgs) {
  const NOM_ATTRIBUT = "métiersFavoris";
  const [rechercheMétier, setRechercheMétier] = useState<string>();
  const [valeurSituationMétiers, setValeurSituationMétiers] = useState<SituationMétiersÉlève>();

  const aperçuMétierVersOptionMétier = useCallback((métier: MétierAperçu) => {
    return {
      valeur: métier.id,
      label: métier.nom,
    };
  }, []);

  const { register, erreurs, mettreÀJourÉlève, setValue, getValues } = useÉlèveForm({
    schémaValidation: métiersValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const métiersSélectionnésParDéfaut = useMemo(() => getValues(NOM_ATTRIBUT), [getValues, valeurSituationMétiers]);

  useEffect(() => {
    if (métiersSélectionnésParDéfaut && métiersSélectionnésParDéfaut.length > 0) {
      setValeurSituationMétiers("quelques_pistes");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const {
    data: métiers,
    refetch: rechercherMétiers,
    isFetching: rechercheMétiersEnCours,
  } = useQuery(rechercheMétiersQueryOptions(rechercheMétier));

  const { data: aperçusMétiersSélectionnésParDéfaut } = useQuery(
    récupérerAperçusMétiersQueryOptions(métiersSélectionnésParDéfaut ?? []),
  );

  useEffect(() => {
    rechercherMétiers();
  }, [rechercheMétier, rechercherMétiers]);

  useEffect(() => {
    if (!métiersSélectionnésParDéfaut || valeurSituationMétiers === "aucune_idee") {
      setValue(NOM_ATTRIBUT, []);
    }
  }, [métiersSélectionnésParDéfaut, setValue, valeurSituationMétiers]);

  const auChangementDesMétiersSélectionnés = (métiersSélectionnés: SélecteurMultipleOption[]) => {
    setValue(
      NOM_ATTRIBUT,
      métiersSélectionnés.map((métier) => métier.valeur),
    );
  };

  const situationMétiersOptions: BoutonRadioRicheProps["options"] = useMemo(
    () => [
      {
        valeur: "quelques_pistes",
        label: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
        pictogramme: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
        cochéParDéfaut: valeurSituationMétiers === "quelques_pistes",
      },
      {
        valeur: "aucune_idee",
        label: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
        description: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
        pictogramme: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
      },
    ],
    [valeurSituationMétiers],
  );

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    situationMétiersOptions,
    valeurSituationMétiers,
    setValeurSituationMétiers,
    métiersSuggérés: métiers?.map(aperçuMétierVersOptionMétier) ?? [],
    métiersSélectionnésParDéfaut: aperçusMétiersSélectionnésParDéfaut?.map(aperçuMétierVersOptionMétier),
    rechercheMétiersEnCours,
    auChangementDesMétiersSélectionnés,
    àLaRechercheDUnMétier: setRechercheMétier,
  };
}
