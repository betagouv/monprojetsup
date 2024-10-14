import useÉtudeForm from "./useÉtudeForm";
import { type ÉtudeFormProps } from "./ÉtudeForm.interface";
import ListeDéroulante from "@/components/ListeDéroulante/ListeDéroulante";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { i18n } from "@/configuration/i18n/i18n";

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
        <ListeDéroulante
          description={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.LABEL}
          options={duréeÉtudesPrévueOptions}
          registerHookForm={register("duréeÉtudesPrévue")}
          status={
            erreurs.duréeÉtudesPrévue ? { type: "erreur", message: erreurs.duréeÉtudesPrévue.message } : undefined
          }
        />
        <ListeDéroulante
          description={i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.LABEL}
          options={alternanceOptions}
          registerHookForm={register("alternance")}
          status={erreurs.alternance ? { type: "erreur", message: erreurs.alternance.message } : undefined}
        />
      </div>
      <div className="mt-12">
        <SélecteurMultiple
          auChangementOptionsSélectionnées={auChangementDesCommunesSélectionnées}
          description={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.LABEL}
          nombreDeCaractèreMinimumRecherche={3}
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
