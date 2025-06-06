import { type IntérêtsFormProps } from "./IntérêtsForm.interface";
import useIntérêtsForm from "./useIntérêtsForm";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { i18n } from "@/configuration/i18n/i18n";

const IntérêtsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId, niveauDeTitreCatégories }: IntérêtsFormProps) => {
  const {
    mettreÀJourÉlève,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut,
    auChangementFiltresSélectionnés,
    légendeId,
  } = useIntérêtsForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <fieldset
        aria-labelledby={`${légendeId} intérêts-message`}
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
      </fieldset>
    </form>
  );
};

export default IntérêtsForm;
