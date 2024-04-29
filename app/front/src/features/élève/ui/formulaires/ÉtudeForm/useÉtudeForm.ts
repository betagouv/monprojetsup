import { type useÉtudeFormArgs } from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: useÉtudeFormArgs) {
  const { mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: étudeValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return {
    mettreÀJourÉlève,
  };
}
