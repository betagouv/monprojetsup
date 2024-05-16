import { type DomainesFormProps } from "./DomainesForm.interface";
import { domainesValidationSchema } from "./DomainesForm.validation";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { i18n } from "@/configuration/i18n/i18n";
import { catégoriesDomainesProfessionnelsQueryOptions } from "@/features/domaineProfessionnel/ui/options";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useSuspenseQuery } from "@tanstack/react-query";
import { useId } from "react";

const DomainesForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: DomainesFormProps) => {
  const { data: catégoriesDomainesProfessionnels } = useSuspenseQuery(catégoriesDomainesProfessionnelsQueryOptions);

  const { setValue, mettreÀJourÉlève, getValues, erreurs } = useÉlèveForm({
    schémaValidation: domainesValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories = catégoriesDomainesProfessionnels.map((catégorie) => ({
    nom: catégorie.nom,
    emoji: catégorie.emoji,
    filtres: catégorie.domainesProfessionnels,
  }));

  const légendeId = useId();

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
          auChangementFiltresSélectionnés={(filtreIdsSélectionnés) => setValue("domaines", filtreIdsSélectionnés)}
          catégories={filtresGroupésParCatégories}
          filtreIdsSélectionnésParDéfaut={getValues("domaines")}
        />
        <div
          aria-live="assertive"
          id="domaines-message"
        >
          {erreurs.domaines && (
            <div className="fr-alert fr-alert--error fr-alert--sm mt-12">
              <p>{erreurs.domaines.message}</p>
            </div>
          )}
        </div>
      </fieldset>
    </form>
  );
};

export default DomainesForm;
