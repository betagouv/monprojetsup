import { type DomainesFormProps } from "./DomainesForm.interface";
import { domainesValidationSchema } from "./DomainesForm.validation";
import FiltresGroupésParCatégorie from "@/components/FiltresGroupésParCatégorie/FiltresGroupésParCatégorie";
import { catégoriesDomainesProfessionnelsQueryOptions } from "@/features/domaineProfessionnel/ui/options";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useSuspenseQuery } from "@tanstack/react-query";

const DomainesForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: DomainesFormProps) => {
  const { data: catégoriesDomainesProfessionnels } = useSuspenseQuery(catégoriesDomainesProfessionnelsQueryOptions);

  const { mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: domainesValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories = catégoriesDomainesProfessionnels.map((catégorie) => ({
    nom: catégorie.nom,
    emoji: catégorie.emoji,
    filtres: catégorie.domainesProfessionnels,
  }));

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      <FiltresGroupésParCatégorie catégories={filtresGroupésParCatégories} />
    </form>
  );
};

export default DomainesForm;
