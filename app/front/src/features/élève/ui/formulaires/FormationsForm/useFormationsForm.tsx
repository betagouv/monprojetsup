import { type UseFormationsFormArgs } from "./FormationsForm.interface";
import { formationsValidationSchema } from "./FormationsForm.validation";
import useFormationsFavoritesFormationsForm from "./useFormationsFavoritesFormationsForm";
import useSituationFormationsFormationsForm from "./useSituationFormationsFormationsForm";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

export default function useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseFormationsFormArgs) {
  const { register, erreurs, mettreÀJourÉlève, setValue, getValues } = useÉlèveForm({
    schémaValidation: formationsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationFormationsFormationsForm = useSituationFormationsFormationsForm({
    setValue,
    getValues,
  });

  const formationsFavoritesFormationsForm = useFormationsFavoritesFormationsForm({
    setValue,
    getValues,
  });

  const soumettreFormulaire = async (event?: React.BaseSyntheticEvent) => {
    const nbFormationsFavorites = getValues(constantes.FORMATIONS.CHAMP_FORMATIONS_FAVORITES)?.length;

    if (!situationFormationsFormationsForm.optionSélectionnée) {
      event?.preventDefault();
      situationFormationsFormationsForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE}`,
      });
      return;
    }

    if (
      situationFormationsFormationsForm.optionSélectionnée === "quelques_pistes" &&
      (!nbFormationsFavorites || nbFormationsFavorites === 0)
    ) {
      event?.preventDefault();
      situationFormationsFormationsForm.modifierStatus({
        type: "error",
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
    situationFormations: situationFormationsFormationsForm,
    ...formationsFavoritesFormationsForm,
  };
}
