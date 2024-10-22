import useÉtudeForm from "./useÉtudeForm";
import { type ÉtudeFormProps } from "./ÉtudeForm.interface";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { Select } from "@codegouvfr/react-dsfr/SelectNext";

const ÉtudeForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ÉtudeFormProps) => {
  const {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
    auChangementDesCommunesSélectionnées,
    communesSuggérées,
    communesSélectionnéesParDéfaut,
    àLaRechercheDUneCommune,
    rechercheCommunesEnCours,
  } = useÉtudeForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <div className="grid grid-flow-row gap-8 md:grid-flow-col">
        <Select
          hint={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.LABEL}
          nativeSelectProps={{ ...register("duréeÉtudesPrévue") }}
          options={duréeÉtudesPrévueOptions}
          state={erreurs.duréeÉtudesPrévue ? "error" : "default"}
          stateRelatedMessage={erreurs.duréeÉtudesPrévue?.message}
        />
        <Select
          hint={i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.LABEL}
          nativeSelectProps={{ ...register("alternance") }}
          options={alternanceOptions}
          state={erreurs.alternance ? "error" : "default"}
          stateRelatedMessage={erreurs.alternance?.message}
        />
      </div>
      <div className="mt-12">
        <SélecteurMultiple
          auChangementOptionsSélectionnées={auChangementDesCommunesSélectionnées}
          description={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.LABEL}
          nombreDeCaractèreMinimumRecherche={constantes.COMMUNES.NB_CARACTÈRES_MIN_RECHERCHE}
          optionsSuggérées={communesSuggérées}
          optionsSélectionnéesParDéfaut={communesSélectionnéesParDéfaut}
          rechercheSuggestionsEnCours={rechercheCommunesEnCours}
          texteOptionsSélectionnées={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.SÉLECTIONNÉES}
          àLaRechercheDUneOption={àLaRechercheDUneCommune}
        />
      </div>
    </form>
  );
};

export default ÉtudeForm;
