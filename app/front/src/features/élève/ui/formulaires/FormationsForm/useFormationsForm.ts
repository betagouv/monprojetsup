import { type SituationFormationsÉlève, type useFormationsFormArgs } from "./FormationsForm.interface";
import { formationsValidationSchema } from "./FormationsForm.validation";
import { type BoutonRadioRicheProps } from "@/components/BoutonRadioRiche/BoutonRadioRiche.interface";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { type Formation } from "@/features/formation/domain/formation.interface";
import {
  rechercherFormationsQueryOptions,
  récupérerFormationsQueryOptions,
} from "@/features/formation/ui/formationQueries";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { type StatusFormulaire } from "@/types/commons";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès }: useFormationsFormArgs) {
  const NOM_ATTRIBUT = "formationsFavorites";

  const [rechercheFormation, setRechercheFormation] = useState<string>();
  const [valeurSituationFormations, setValeurSituationFormations] = useState<SituationFormationsÉlève>();
  const [statusSituationFormations, setStatusSituationFormations] = useState<StatusFormulaire>();

  const formationVersOptionFormation = useCallback((formation: Formation) => {
    return {
      valeur: formation.id,
      label: formation.nom,
    };
  }, []);

  const { register, erreurs, mettreÀJourÉlève, setValue, getValues } = useÉlèveForm({
    schémaValidation: formationsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const formationIdsSélectionnéesParDéfaut = useMemo(
    () => getValues(NOM_ATTRIBUT)?.map((formation) => formation.id),
    [getValues],
  );

  useEffect(() => {
    if (formationIdsSélectionnéesParDéfaut && formationIdsSélectionnéesParDéfaut.length > 0) {
      setValeurSituationFormations("quelques_pistes");
    }
  }, []);

  const {
    data: formations,
    refetch: rechercherFormations,
    isFetching: rechercheFormationsEnCours,
  } = useQuery(rechercherFormationsQueryOptions(rechercheFormation));

  const { data: formationsSélectionnéesParDéfaut } = useQuery(
    récupérerFormationsQueryOptions(formationIdsSélectionnéesParDéfaut ?? []),
  );

  useEffect(() => {
    if (rechercheFormation && rechercheFormation.length >= constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE) {
      void rechercherFormations();
    }
  }, [rechercheFormation, rechercherFormations]);

  useEffect(() => {
    setStatusSituationFormations(undefined);

    if (!formationsSélectionnéesParDéfaut || valeurSituationFormations === "aucune_idee") {
      setValue(NOM_ATTRIBUT, []);
    }
  }, [formationsSélectionnéesParDéfaut, setValue, valeurSituationFormations]);

  const auChangementDesFormationsSélectionnées = (formationsSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      NOM_ATTRIBUT,
      formationsSélectionnées.map((formation) => ({
        id: formation.valeur,
        niveauAmbition: null,
        voeux: [],
        commentaire: null,
      })),
    );
  };

  const situationFormationsOptions: BoutonRadioRicheProps["options"] = useMemo(
    () => [
      {
        valeur: "quelques_pistes",
        label: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
        pictogramme: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
        cochéParDéfaut: valeurSituationFormations === "quelques_pistes",
      },
      {
        valeur: "aucune_idee",
        label: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
        description: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
        pictogramme: i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
      },
    ],
    [valeurSituationFormations],
  );

  const soumettreFormulaire = async (event?: React.BaseSyntheticEvent) => {
    const nombreDeFormationsSélectionnées = getValues(NOM_ATTRIBUT)?.length;

    if (
      valeurSituationFormations === "quelques_pistes" &&
      (!nombreDeFormationsSélectionnées || nombreDeFormationsSélectionnées === 0)
    ) {
      event?.preventDefault();
      setStatusSituationFormations({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_UNE} ${i18n.COMMUN.FORMATION.toLocaleLowerCase()}`,
      });
      return;
    }

    await mettreÀJourÉlève(event);
  };

  return {
    mettreÀJourÉlève: soumettreFormulaire,
    erreurs,
    register,
    situationFormations: {
      valeur: valeurSituationFormations,
      status: statusSituationFormations,
      options: situationFormationsOptions,
      auChangement: setValeurSituationFormations,
    },
    formationsSuggérées: formations?.map(formationVersOptionFormation) ?? [],
    formationsSélectionnéesParDéfaut: formationsSélectionnéesParDéfaut?.map(formationVersOptionFormation),
    rechercheFormationsEnCours,
    auChangementDesFormationsSélectionnées,
    àLaRechercheDUneFormation: setRechercheFormation,
  };
}
