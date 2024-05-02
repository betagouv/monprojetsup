import { type SituationFormationsOptions, type useFormationsFormArgs } from "./FormationsForm.interface";
import { formationsValidationSchema } from "./FormationsForm.validation";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { type FormationAperçu } from "@/features/formation/domain/formation.interface";
import {
  rechercheFormationsQueryOptions,
  récupérerAperçusFormationsQueryOptions,
} from "@/features/formation/ui/options";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès }: useFormationsFormArgs) {
  const [rechercheFormation, setRechercheFormation] = useState<string>();

  const aperçuFormationVersOptionFormation = useCallback((formation: FormationAperçu) => {
    return {
      valeur: formation.id,
      label: formation.nom,
    };
  }, []);

  const { register, erreurs, mettreÀJourÉlève, watch, setValue, getValues } = useÉlèveForm({
    schémaValidation: formationsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const valeurSituationFormations = watch("situationFormations");

  const formationsSélectionnéesParDéfaut = useMemo(
    () => getValues("formations"),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [getValues, valeurSituationFormations],
  );

  const {
    data: formations,
    refetch: rechercherFormations,
    isFetching: rechercheFormationsEnCours,
  } = useQuery(rechercheFormationsQueryOptions(rechercheFormation));

  const { data: aperçusFormationsSélectionnéesParDéfaut } = useQuery(
    récupérerAperçusFormationsQueryOptions(formationsSélectionnéesParDéfaut),
  );

  useEffect(() => {
    rechercherFormations();
  }, [rechercheFormation, rechercherFormations]);

  useEffect(() => {
    if (valeurSituationFormations === "aucune_idee") {
      setValue("formations", []);
    }
  }, [setValue, valeurSituationFormations]);

  const auChangementDesFormationsSélectionnées = (formationsSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      "formations",
      formationsSélectionnées.map((formation) => formation.valeur),
    );
  };

  const situationFormationsOptions: SituationFormationsOptions = [
    {
      valeur: "quelques_pistes",
      label: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      pictogramme: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
    },
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
      description: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
    },
  ];

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    situationFormationsOptions,
    valeurSituationFormations,
    formationsSuggérées: formations?.map(aperçuFormationVersOptionFormation) ?? [],
    formationsSélectionnéesParDéfaut: aperçusFormationsSélectionnéesParDéfaut?.map(aperçuFormationVersOptionFormation),
    rechercheFormationsEnCours,
    auChangementDesFormationsSélectionnées,
    àLaRechercheDUneFormation: setRechercheFormation,
  };
}
