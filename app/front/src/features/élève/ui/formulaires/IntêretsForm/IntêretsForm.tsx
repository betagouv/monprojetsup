import { type IntêretsFormProps } from "./IntêretsForm.interface";
import { intêretsValidationSchema } from "./IntêretsForm.validation";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

const IntêretsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: IntêretsFormProps) => {
  const { mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: intêretsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      Je suis le formulaire intêret
    </form>
  );
};

export default IntêretsForm;
