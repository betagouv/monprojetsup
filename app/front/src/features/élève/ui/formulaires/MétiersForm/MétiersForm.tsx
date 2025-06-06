import { type MétiersFormProps } from "./MétiersForm.interface";
import useMétiersForm from "./useMétiersForm";
import { i18n } from "@/configuration/i18n/i18n";
import MaSélectionMétiers from "@/features/métier/ui/SélectionMétiersFavoris/MaSélectionMétiers/MaSélectionMétiers";
import RechercheMétiers from "@/features/métier/ui/SélectionMétiersFavoris/RechercheMétiers/RechercheMétiers";
import { RadioButtons } from "@codegouvfr/react-dsfr/RadioButtons";

const MétiersForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: MétiersFormProps) => {
  const { mettreÀJourÉlève, situationMétiers } = useMétiersForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <RadioButtons
        hintText={i18n.ÉLÈVE.MÉTIERS.SITUATION.DESCRIPTION}
        legend={i18n.ÉLÈVE.MÉTIERS.SITUATION.LÉGENDE}
        options={situationMétiers.options}
        orientation="horizontal"
        state={situationMétiers.status.type}
        stateRelatedMessage={situationMétiers.status.message}
      />
      {situationMétiers.optionSélectionnée === "quelques_pistes" && (
        <fieldset className="mt-12 grid gap-6 border-0 p-0">
          <RechercheMétiers />
          <MaSélectionMétiers />
        </fieldset>
      )}
    </form>
  );
};

export default MétiersForm;
