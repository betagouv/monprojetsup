import { type ScolaritéFormProps } from "./ScolaritéForm.interface";
import MaSélectionSpécialités from "./Spécialités/MaSélectionSpécialités/MaSélectionSpécialités";
import RechercheSpécialités from "./Spécialités/RechercheSpécialités/RechercheSpécialités";
import useScolaritéForm from "./useScolaritéForm";
import { i18n } from "@/configuration/i18n/i18n";
import { Select } from "@codegouvfr/react-dsfr/SelectNext";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import { useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";

const ScolaritéForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ScolaritéFormProps) => {

  const { data: élève } = useQuery(élèveQueryOptions);
  
  const {
    mettreÀJourÉlève,
    erreurs,
    register,
    classeOptions,
    bacOptions,
    valeurBac,
    spécialitésBac
  } = useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès });

  useEffect(() => {

  }, [élève, register]);

  if(!élève) return (<AnimationChargement />);

  return (
    <form
      className="grid gap-12"
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <div className="grid grid-flow-row gap-8 md:grid-cols-[1fr_1fr]">
        <Select
          label={i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LABEL}
          nativeSelectProps={{ required: true, ...register("classe") }}
          options={classeOptions}
          state={erreurs.classe ? "error" : "default"}
          stateRelatedMessage={erreurs.classe?.message}
        />
        <Select
          label={i18n.ÉLÈVE.SCOLARITÉ.BAC.LABEL}
          nativeSelectProps={{ ...register("bac") }}
          options={bacOptions}
          state={erreurs.bac ? "error" : "default"}
          stateRelatedMessage={erreurs.bac?.message}
        />
      </div>
      {valeurBac && spécialitésBac?.length > 0 && (
        <fieldset className="grid gap-6 border-0 p-0">
          <RechercheSpécialités
            bac={valeurBac}
            key={valeurBac}
            spécialitésBac={spécialitésBac}
          />
          <MaSélectionSpécialités />
        </fieldset>
      )}
    </form>
  );
};

export default ScolaritéForm;
