import { type ÉtudeFormProps } from "./ÉtudeForm.interface";
import useÉtudeForm from "./useÉtudeForm";
import BoutonRadio from "@/components/_dsfr/BoutonRadio/BoutonRadio";
import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtudeForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ÉtudeFormProps) => {
  const {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
    situationVillesOptions,
    valeurSituationVilles,
    auChangementDesVillesSélectionnées,
    villesSuggérées,
    villesSélectionnéesParDéfaut,
    àLaRechercheDUneVille,
    rechercheVillesEnCours,
  } = useÉtudeForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
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
        <BoutonRadio
          légende={i18n.ÉLÈVE.ÉTUDE.SITUATION_VILLES.LÉGENDE}
          options={situationVillesOptions}
          registerHookForm={register("situationVilles")}
          status={erreurs.situationVilles ? { type: "erreur", message: erreurs.situationVilles.message } : undefined}
        />
        {valeurSituationVilles === "quelques_pistes" && villesSélectionnéesParDéfaut && (
          <div className="mt-6">
            <SélecteurMultiple
              auChangementOptionsSélectionnées={auChangementDesVillesSélectionnées}
              description={i18n.ÉLÈVE.ÉTUDE.VILLES_ENVISAGÉES.DESCRIPTION}
              label={i18n.ÉLÈVE.ÉTUDE.VILLES_ENVISAGÉES.LABEL}
              optionsSuggérées={villesSuggérées}
              optionsSélectionnéesParDéfaut={villesSélectionnéesParDéfaut}
              rechercheSuggestionsEnCours={rechercheVillesEnCours}
              texteOptionsSélectionnées={i18n.ÉLÈVE.ÉTUDE.VILLES_ENVISAGÉES.SÉLECTIONNÉES}
              àLaRechercheDUneOption={àLaRechercheDUneVille}
            />
          </div>
        )}
      </div>
    </form>
  );
};

export default ÉtudeForm;
