import { type IntêretsFormProps } from "./IntêretsForm.interface";
import { centresIntêretsValidationSchema } from "./IntêretsForm.validation";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { catégoriesCentresIntêretsQueryOptions } from "@/features/centreIntêret/ui/options";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useSuspenseQuery } from "@tanstack/react-query";

const IntêretsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: IntêretsFormProps) => {
  const { data: catégoriesCentresIntêrets } = useSuspenseQuery(catégoriesCentresIntêretsQueryOptions);

  const { setValue, mettreÀJourÉlève, getValues } = useÉlèveForm({
    schémaValidation: centresIntêretsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories = catégoriesCentresIntêrets.map((catégorie) => ({
    nom: catégorie.nom,
    emoji: catégorie.emoji,
    filtres: catégorie.centresIntêrets,
  }));

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      <FiltresGroupésParCatégorie
        auChangementFiltresSélectionnés={(filtreIdsSélectionnés) => setValue("centresIntêrets", filtreIdsSélectionnés)}
        catégories={filtresGroupésParCatégories}
        filtreIdsSélectionnésParDéfaut={getValues("centresIntêrets")}
      />
    </form>
  );
};

export default IntêretsForm;
