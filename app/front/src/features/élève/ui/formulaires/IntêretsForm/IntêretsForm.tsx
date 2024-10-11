import { type IntêretsFormProps } from "./IntêretsForm.interface";
import useIntêretsForm from "./useIntêretsForm";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { i18n } from "@/configuration/i18n/i18n";

const IntêretsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId, niveauDeTitreCatégories }: IntêretsFormProps) => {
  const {
    mettreÀJourÉlève,
    erreurs,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut,
    auChangementFiltresSélectionnés,
    légendeId,
  } = useIntêretsForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <fieldset
        aria-labelledby={`${légendeId} intêrets-message`}
        className="border-0 p-0"
      >
        <legend
          className="sr-only"
          id={légendeId}
        >
          {i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE}
        </legend>
        <FiltresGroupésParCatégorie
          auChangementFiltresSélectionnés={auChangementFiltresSélectionnés}
          catégories={filtresGroupésParCatégories}
          filtreIdsSélectionnésParDéfaut={filtreIdsSélectionnésParDéfaut}
          niveauDeTitre={niveauDeTitreCatégories}
        />
        <div
          aria-live="assertive"
          id="intêrets-message"
        >
          {erreurs.centresIntêrets && (
            <div className="fr-alert fr-alert--error fr-alert--sm mt-12">
              <p>{erreurs.centresIntêrets.message}</p>
            </div>
          )}
        </div>
      </fieldset>
    </form>
  );
};

export default IntêretsForm;
