import { type UseMétiersFormArgs } from "./MétiersForm.interface";
import useSituationMétiersMétiersForm from "./useSituationMétiersMétiersForm";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";

export default function useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseMétiersFormArgs) {
  const { élèveAuMoinsUnMétierFavori } = useÉlève();
  const { supprimerTousLesMétiersÉlève } = useÉlèveMutation();
  const { setValue, getValues, handleSubmit } = useÉlèveForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationMétiersMétiersForm = useSituationMétiersMétiersForm({
    setValue,
    getValues,
  });

  const soumettreFormulaire = async (event?: React.BaseSyntheticEvent) => {
    event?.preventDefault();

    if (!situationMétiersMétiersForm.optionSélectionnée) {
      situationMétiersMétiersForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE}`,
      });
      return;
    }

    if (situationMétiersMétiersForm.optionSélectionnée === "quelques_pistes" && !élèveAuMoinsUnMétierFavori) {
      situationMétiersMétiersForm.modifierStatus({
        type: "error",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_UN} ${i18n.COMMUN.MÉTIER.toLocaleLowerCase()}`,
      });
      return;
    }

    if (situationMétiersMétiersForm.optionSélectionnée === "aucune_idee") {
      await supprimerTousLesMétiersÉlève();
    }

    await handleSubmit(async () => {
      await àLaSoumissionDuFormulaireAvecSuccès?.();
    })();
  };

  return {
    mettreÀJourÉlève: soumettreFormulaire,
    situationMétiers: situationMétiersMétiersForm,
  };
}
