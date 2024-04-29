import { type ÉtudeFormProps } from "./ÉtudeForm.interface";
import useÉtudeForm from "./useÉtudeForm";

const ÉtudeForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ÉtudeFormProps) => {
  const { mettreÀJourÉlève } = useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      Je suis le formulaire étude
    </form>
  );
};

export default ÉtudeForm;
