import { type FormationsFormProps } from "./FormationsForm.interface";
import useFormationsForm from "./useFormationsForm";
import { i18n } from "@/configuration/i18n/i18n";
import MaSélectionFormations from "@/features/formation/ui/SélectionFormationsFavorites/MaSélectionFormations/MaSélectionFormations.tsx";
import RechercheFormations from "@/features/formation/ui/SélectionFormationsFavorites/RechercheFormations/RechercheFormations.tsx";
import { RadioButtons } from "@codegouvfr/react-dsfr/RadioButtons";

const FormationsForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: FormationsFormProps) => {
  const { mettreÀJourÉlève, situationFormations } = useFormationsForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <RadioButtons
        legend={i18n.ÉLÈVE.FORMATIONS.SITUATION.LÉGENDE}
        options={situationFormations.options}
        orientation="horizontal"
        state={situationFormations.status.type}
        stateRelatedMessage={situationFormations.status.message}
      />
      {situationFormations.optionSélectionnée === "quelques_pistes" && (
        <fieldset className="mt-12 grid gap-6 border-0 p-0">
          <RechercheFormations />
          <MaSélectionFormations />
        </fieldset>
      )}
    </form>
  );
};

export default FormationsForm;
