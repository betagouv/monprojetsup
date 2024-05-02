import { type ÉtudeFormProps } from "./ÉtudeForm.interface";
import useÉtudeForm from "./useÉtudeForm";
import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";
import { i18n } from "@/configuration/i18n/i18n";

const ÉtudeForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ÉtudeFormProps) => {
  const { mettreÀJourÉlève, duréeÉtudesPrévueOptions, alternanceOptions, erreurs, register } = useÉtudeForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      <div className="grid grid-flow-row gap-8 md:grid-flow-col">
        <ListeDéroulante
          description={i18n.ÉLÈVE.ÉTUDES.DURÉE_ETUDES.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDES.DURÉE_ETUDES.LABEL}
          options={duréeÉtudesPrévueOptions}
          registerHookForm={register("duréeÉtudesPrévue")}
          status={
            erreurs.duréeÉtudesPrévue ? { type: "erreur", message: erreurs.duréeÉtudesPrévue.message } : undefined
          }
        />
        <ListeDéroulante
          description={i18n.ÉLÈVE.ÉTUDES.ALTERNANCE.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDES.ALTERNANCE.LABEL}
          options={alternanceOptions}
          registerHookForm={register("alternance")}
          status={erreurs.alternance ? { type: "erreur", message: erreurs.alternance.message } : undefined}
        />
      </div>
    </form>
  );
};

export default ÉtudeForm;
