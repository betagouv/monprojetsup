import { type UseFormationsFormArgs } from "./FormationsForm.interface";
import useSituationFormationsFormationsForm from "./useSituationFormationsFormationsForm";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";

export default function useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseFormationsFormArgs) {
  const { élèveAuMoinsUneFormationFavorite } = useÉlève();
  const { supprimerToutesLesFormationsÉlève } = useÉlèveMutation();
  const { setValue, getValues, handleSubmit } = useÉlèveForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationFormationsFormationsForm = useSituationFormationsFormationsForm({
    setValue,
    getValues,
  });

  const soumettreFormulaire = async (event?: React.BaseSyntheticEvent) => {
    event?.preventDefault();

    if (!situationFormationsFormationsForm.optionSélectionnée) {
      situationFormationsFormationsForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE}`,
      });
      return;
    }

    if (
      situationFormationsFormationsForm.optionSélectionnée === "quelques_pistes" &&
      !élèveAuMoinsUneFormationFavorite
    ) {
      situationFormationsFormationsForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_UNE} ${i18n.COMMUN.FORMATION.toLocaleLowerCase()}`,
      });
      return;
    }

    if (situationFormationsFormationsForm.optionSélectionnée === "aucune_idee") {
      await supprimerToutesLesFormationsÉlève();
    }

    await handleSubmit(async () => {
      await àLaSoumissionDuFormulaireAvecSuccès?.();
    })();
  };

  return {
    mettreÀJourÉlève: soumettreFormulaire,
    situationFormations: situationFormationsFormationsForm,
  };
}
