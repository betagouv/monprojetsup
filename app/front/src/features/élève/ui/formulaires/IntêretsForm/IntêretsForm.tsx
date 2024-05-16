import { type IntêretsFormProps } from "./IntêretsForm.interface";
import { centresIntêretsValidationSchema } from "./IntêretsForm.validation";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { i18n } from "@/configuration/i18n/i18n";
import { catégoriesCentresIntêretsQueryOptions } from "@/features/centreIntêret/ui/options";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useSuspenseQuery } from "@tanstack/react-query";
import { useId } from "react";

const IntêretsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: IntêretsFormProps) => {
  const { data: catégoriesCentresIntêrets } = useSuspenseQuery(catégoriesCentresIntêretsQueryOptions);

  const { setValue, mettreÀJourÉlève, getValues, erreurs } = useÉlèveForm({
    schémaValidation: centresIntêretsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories = catégoriesCentresIntêrets.map((catégorie) => ({
    nom: catégorie.nom,
    emoji: catégorie.emoji,
    filtres: catégorie.centresIntêrets,
  }));

  const légendeId = useId();

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
          {i18n.ÉLÈVE.INTÊRETS.PARCOURS_INSCRIPTION.TITRE}
        </legend>
        <FiltresGroupésParCatégorie
          auChangementFiltresSélectionnés={(filtreIdsSélectionnés) =>
            setValue("centresIntêrets", filtreIdsSélectionnés)
          }
          catégories={filtresGroupésParCatégories}
          filtreIdsSélectionnésParDéfaut={getValues("centresIntêrets")}
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
