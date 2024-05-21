import { type ProjetFormProps, type SituationOptions } from "./ProjetForm.interface";
import { projetValidationSchema } from "./ProjetForm.validation";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

const ProjetForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ProjetFormProps) => {
  const { register, erreurs, mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: projetValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationOptions: SituationOptions = [
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
    },
    {
      valeur: "quelques_pistes",
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.QUELQUES_PISTES.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
    },
    {
      valeur: "projet_precis",
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.PROJET_PRÉCIS.LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.PROJET_PRÉCIS.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.PROJET_PRÉCIS.EMOJI,
    },
  ];

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <BoutonRadioRiche
        légende={i18n.ÉLÈVE.PROJET.SITUATION.LÉGENDE}
        obligatoire
        options={situationOptions}
        registerHookForm={register("situation")}
        status={erreurs.situation ? { type: "erreur", message: erreurs.situation.message } : undefined}
      />
    </form>
  );
};

export default ProjetForm;
