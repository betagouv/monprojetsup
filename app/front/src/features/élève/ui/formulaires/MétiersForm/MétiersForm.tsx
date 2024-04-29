import { type MétiersFormProps } from "./MétiersForm.interface";
import useMétiersForm from "./useMétiersForm";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import SélecteurMultipleAvecAppelHttp from "@/components/SélecteurMultipleAvecAppelHttp/SélecteurMultipleAvecAppelHttp";
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
      onSubmit={mettreÀJourÉlève}
    >
      <BoutonRadioRiche
        légende={i18n.ÉLÈVE.MÉTIERS.SITUATION.LÉGENDE}
        options={situationMétiersOptions}
        registerHookForm={register("situationMétiers")}
        status={erreurs.situationMétiers ? { type: "erreur", message: erreurs.situationMétiers.message } : undefined}
      />
      {valeurSituationMétiers === "quelques_pistes" && métiersSélectionnésParDéfaut && (
        <div className="mt-12">
          <SélecteurMultipleAvecAppelHttp
            auChangementOptionsSélectionnées={auChangementDesMétiersSélectionnés}
            description={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.DESCRIPTION}
            label={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL}
            optionsSuggérées={métiersSuggérés}
            optionsSélectionnéesParDéfaut={métiersSélectionnésParDéfaut}
            rechercheMétiersEnCours={rechercheMétiersEnCours}
            texteOptionsSélectionnées={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.SÉLECTIONNÉS}
            àLaRechercheDUneOption={àLaRechercheDUnMétier}
          />
        </div>
      )}
    </form>
  );
};

export default MétiersForm;
