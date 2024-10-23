import { type MétiersFormProps } from "./MétiersForm.interface";
import useMétiersForm from "./useMétiersForm";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { RadioButtons } from "@codegouvfr/react-dsfr/RadioButtons";

const MétiersForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: MétiersFormProps) => {
  const {
    mettreÀJourÉlève,
    situationMétiers,
    auChangementDesMétiersSélectionnés,
    métiersSuggérés,
    métiersSélectionnésParDéfaut,
    àLaRechercheDUnMétier,
    rechercheEnCours,
  } = useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <RadioButtons
        hintText={i18n.ÉLÈVE.MÉTIERS.SITUATION.DESCRIPTION}
        legend={i18n.ÉLÈVE.MÉTIERS.SITUATION.LÉGENDE}
        options={situationMétiers.options}
        orientation="horizontal"
        state={situationMétiers.status.type}
        stateRelatedMessage={situationMétiers.status.message}
      />
      {situationMétiers.optionSélectionnée === "quelques_pistes" && (
        <div className="mt-12">
          <SélecteurMultiple
            auChangementOptionsSélectionnées={auChangementDesMétiersSélectionnés}
            description={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.DESCRIPTION}
            forcerRafraichissementOptionsSélectionnées
            label={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL}
            nombreDeCaractèreMinimumRecherche={constantes.MÉTIERS.NB_CARACTÈRES_MIN_RECHERCHE}
            optionsSuggérées={métiersSuggérés}
            optionsSélectionnéesParDéfaut={métiersSélectionnésParDéfaut}
            rechercheSuggestionsEnCours={rechercheEnCours}
            texteOptionsSélectionnées={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.SÉLECTIONNÉS}
            àLaRechercheDUneOption={àLaRechercheDUnMétier}
          />
        </div>
      )}
    </form>
  );
};

export default MétiersForm;
