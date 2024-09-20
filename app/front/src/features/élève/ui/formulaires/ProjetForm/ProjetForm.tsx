import { type ProjetFormProps } from "./ProjetForm.interface";
import useProjetForm from "./useProjetForm";
import BoutonRadioRiche from "@/components/BoutonRadioRiche/BoutonRadioRiche";
import { i18n } from "@/configuration/i18n/i18n";

const ProjetForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ProjetFormProps) => {
  const { mettreÀJourÉlève, erreurs, register, situationOptions } = useProjetForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <BoutonRadioRiche
        légende={i18n.ÉLÈVE.PROJET.SITUATION.LÉGENDE}
        obligatoire
        options={situationOptions}
        registerHookForm={register("situation")}
        status={erreurs.situation ? { type: "erreur", message: erreurs.situation.message } : undefined}
      />
    </form>
  );
};

export default ProjetForm;
