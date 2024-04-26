import { type MétiersFormProps, type SituationOptions } from "./MétiersForm.interface";
import { métiersValidationSchema } from "./MétiersForm.validation";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

const MétiersForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: MétiersFormProps) => {
  const { register, erreurs, mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: métiersValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationOptions: SituationOptions = [
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

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      <BoutonRadioRiche
        légende={i18n.ÉLÈVE.MÉTIERS.SITUATION.LÉGENDE}
        options={situationOptions}
        registerHookForm={register("situationMétiers")}
        status={erreurs.situationMétiers ? { type: "erreur", message: erreurs.situationMétiers.message } : undefined}
      />
    </form>
  );
};

export default MétiersForm;
