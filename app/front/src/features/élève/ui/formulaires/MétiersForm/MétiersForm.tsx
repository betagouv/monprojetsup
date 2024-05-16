import { type MétiersFormProps } from "./MétiersForm.interface";
import useMétiersForm from "./useMétiersForm";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { i18n } from "@/configuration/i18n/i18n";

const MétiersForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: MétiersFormProps) => {
  const {
    mettreÀJourÉlève,
    situationMétiersOptions,
    erreurs,
    register,
    valeurSituationMétiers,
    auChangementDesMétiersSélectionnés,
    métiersSuggérés,
    métiersSélectionnésParDéfaut,
    àLaRechercheDUnMétier,
    rechercheMétiersEnCours,
  } = useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <BoutonRadioRiche
        description={i18n.ÉLÈVE.MÉTIERS.SITUATION.DESCRIPTION}
        légende={i18n.ÉLÈVE.MÉTIERS.SITUATION.LÉGENDE}
        obligatoire
        options={situationMétiersOptions}
        registerHookForm={register("situationMétiers")}
        status={erreurs.situationMétiers ? { type: "erreur", message: erreurs.situationMétiers.message } : undefined}
      />
      {valeurSituationMétiers === "quelques_pistes" && métiersSélectionnésParDéfaut && (
        <div className="mt-12">
          <SélecteurMultiple
            auChangementOptionsSélectionnées={auChangementDesMétiersSélectionnés}
            description={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.DESCRIPTION}
            label={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL}
            optionsSuggérées={métiersSuggérés}
            optionsSélectionnéesParDéfaut={métiersSélectionnésParDéfaut}
            rechercheSuggestionsEnCours={rechercheMétiersEnCours}
            texteOptionsSélectionnées={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.SÉLECTIONNÉS}
            àLaRechercheDUneOption={àLaRechercheDUnMétier}
          />
        </div>
      )}
    </form>
  );
};

export default MétiersForm;
