import { type FormationsFormProps } from "./FormationsForm.interface";
import useFormationsForm from "./useFormationsForm";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { RadioButtons } from "@codegouvfr/react-dsfr/RadioButtons";

const FormationsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: FormationsFormProps) => {
  const {
    mettreÀJourÉlève,
    situationFormations,
    auChangementDesFormationsSélectionnées,
    formationsSuggérées,
    formationsSélectionnéesParDéfaut,
    àLaRechercheDUneFormation,
    rechercheEnCours,
  } = useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <RadioButtons
        legend={i18n.ÉLÈVE.FORMATIONS.SITUATION.LÉGENDE}
        options={situationFormations.options}
        orientation="horizontal"
        state={situationFormations.status.type}
        stateRelatedMessage={situationFormations.status.message}
      />
      {situationFormations.optionSélectionnée === "quelques_pistes" && (
        <div className="mt-12">
          <SélecteurMultiple
            auChangementOptionsSélectionnées={auChangementDesFormationsSélectionnées}
            description={i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.DESCRIPTION}
            forcerRafraichissementOptionsSélectionnées
            label={i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.LABEL}
            nombreDeCaractèreMinimumRecherche={constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE}
            optionsSuggérées={formationsSuggérées}
            optionsSélectionnéesParDéfaut={formationsSélectionnéesParDéfaut}
            rechercheSuggestionsEnCours={rechercheEnCours}
            texteOptionsSélectionnées={i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.SÉLECTIONNÉES}
            àLaRechercheDUneOption={àLaRechercheDUneFormation}
          />
        </div>
      )}
    </form>
  );
};

export default FormationsForm;
