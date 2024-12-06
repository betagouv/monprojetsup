import { type UseMétiersFormArgs } from "./MétiersForm.interface";
import { métiersValidationSchema } from "./MétiersForm.validation";
import useSituationMétiersMétiersForm from "./useSituationMétiersMétiersForm";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useEffect } from "react";

export default function useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseMétiersFormArgs) {
  const { mettreÀJourMétiersFavorisÉlève, élève } = useÉlève();
  const { setValue, getValues, handleSubmit } = useÉlèveForm({
    schémaValidation: métiersValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationMétiersMétiersForm = useSituationMétiersMétiersForm({
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

    if (situationMétiersMétiersForm.optionSélectionnée === "aucune_idee" && élève?.métiersFavoris) {
      await mettreÀJourMétiersFavorisÉlève(élève?.métiersFavoris);
    }

    await handleSubmit(async () => {
      await àLaSoumissionDuFormulaireAvecSuccès?.();
    })();
  };

  // Garder synchronisé la valeur react-hook-form et le profil de l'élève
  useEffect(() => {
    setValue("métiersFavoris", élève?.métiersFavoris ?? []);
  }, [setValue, élève?.métiersFavoris]);

  return {
    mettreÀJourÉlève: soumettreFormulaire,
    situationMétiers: situationMétiersMétiersForm,
  };
}
