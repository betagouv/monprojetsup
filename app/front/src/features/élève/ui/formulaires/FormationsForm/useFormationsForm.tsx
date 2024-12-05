import { type UseFormationsFormArgs } from "./FormationsForm.interface";
import { formationsValidationSchema } from "./FormationsForm.validation";
import useSituationFormationsFormationsForm from "./useSituationFormationsFormationsForm";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useEffect } from "react";

export default function useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseFormationsFormArgs) {
  const { mettreÀJourFormationsFavoritesÉlève, élève } = useÉlève({});
  const { setValue, getValues, handleSubmit } = useÉlèveForm({
    schémaValidation: formationsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationFormationsFormationsForm = useSituationFormationsFormationsForm({
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

    if (situationFormationsFormationsForm.optionSélectionnée === "aucune_idee" && élève?.formationsFavorites) {
      await mettreÀJourFormationsFavoritesÉlève(élève?.formationsFavorites?.map(({ id }) => id));
    }

    await handleSubmit(async () => {
      await àLaSoumissionDuFormulaireAvecSuccès?.();
    })();
  };

  // Garder synchroniser la valeur react-hook-form et le profil de l'élève
  useEffect(() => {
    setValue("formationsFavorites", élève?.formationsFavorites ?? []);
  }, [setValue, élève?.formationsFavorites]);

  return {
    mettreÀJourÉlève: soumettreFormulaire,
    situationFormations: situationFormationsFormationsForm,
  };
}
