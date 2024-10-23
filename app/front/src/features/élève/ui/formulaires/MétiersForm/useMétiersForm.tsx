import { type UseMétiersFormArgs } from "./MétiersForm.interface";
import { métiersValidationSchema } from "./MétiersForm.validation";
import useMétiersFavorisMétiersForm from "./useMétiersFavorisMétiersForm";
import useSituationMétiersMétiersForm from "./useSituationMétiersMétiersForm";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

export default function useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseMétiersFormArgs) {
  const { register, erreurs, mettreÀJourÉlève, setValue, getValues } = useÉlèveForm({
    schémaValidation: métiersValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationMétiersMétiersForm = useSituationMétiersMétiersForm({
    setValue,
    getValues,
  });

  const métiersFavorisMétiersForm = useMétiersFavorisMétiersForm({
    setValue,
    getValues,
  });

  const soumettreFormulaire = async (event?: React.BaseSyntheticEvent) => {
    const nbMétiersFavoris = getValues(constantes.MÉTIERS.CHAMP_MÉTIERS_FAVORIS)?.length;

    if (!situationMétiersMétiersForm.optionSélectionnée) {
      event?.preventDefault();
      situationMétiersMétiersForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE}`,
      });
      return;
    }

    if (
      situationMétiersMétiersForm.optionSélectionnée === "quelques_pistes" &&
      (!nbMétiersFavoris || nbMétiersFavoris === 0)
    ) {
      event?.preventDefault();
      situationMétiersMétiersForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_UN} ${i18n.COMMUN.MÉTIER.toLocaleLowerCase()}`,
      });
      return;
    }

    await mettreÀJourÉlève(event);
  };

  return {
    mettreÀJourÉlève: soumettreFormulaire,
    erreurs,
    register,
    situationMétiers: situationMétiersMétiersForm,
    ...métiersFavorisMétiersForm,
  };
}
