import { type ÉtudeFormProps } from "./ÉtudeForm.interface";
import useÉtudeForm from "./useÉtudeForm";
import { i18n } from "@/configuration/i18n/i18n";
import MaSélectionCommunes from "@/features/commune/ui/MaSélectionCommunes/MaSélectionCommunes";
import RechercheCommunes from "@/features/commune/ui/RechercheCommunes/RechercheCommunes";
import { Select } from "@codegouvfr/react-dsfr/SelectNext";

const ÉtudeForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ÉtudeFormProps) => {
  const { mettreÀJourÉlève, erreurs, register, duréeÉtudesPrévueOptions, alternanceOptions } = useÉtudeForm({
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <div className="grid grid-flow-row gap-8 md:grid-flow-col">
        <Select
          hint={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.LABEL}
          nativeSelectProps={{ ...register("duréeÉtudesPrévue") }}
          options={duréeÉtudesPrévueOptions}
          state={erreurs.duréeÉtudesPrévue ? "error" : "default"}
          stateRelatedMessage={erreurs.duréeÉtudesPrévue?.message}
        />
        <Select
          hint={i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.DESCRIPTION}
          label={i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.LABEL}
          nativeSelectProps={{ ...register("alternance") }}
          options={alternanceOptions}
          state={erreurs.alternance ? "error" : "default"}
          stateRelatedMessage={erreurs.alternance?.message}
        />
      </div>
      <fieldset className="mt-12 grid gap-6 border-0 p-0">
        <RechercheCommunes />
        <MaSélectionCommunes />
      </fieldset>
    </form>
  );
};

export default ÉtudeForm;
