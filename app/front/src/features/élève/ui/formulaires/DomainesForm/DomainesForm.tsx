import { type DomainesFormProps } from "./DomainesForm.interface";
import useDomainesForm from "./useDomainesForm";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { i18n } from "@/configuration/i18n/i18n";

const DomainesForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId, niveauDeTitreCatégories }: DomainesFormProps) => {
  const {
    mettreÀJourÉlève,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut,
    auChangementFiltresSélectionnés,
    légendeId,
  } = useDomainesForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <fieldset
        aria-labelledby={`${légendeId} domaines-message`}
        className="border-0 p-0"
      >
        <legend
          className="sr-only"
          id={légendeId}
        >
          {i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE}
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

export default DomainesForm;
