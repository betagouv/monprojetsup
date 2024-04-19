import { type ClasseOptions, type ScolaritéFormProps } from "./ScolaritéForm.interface";
import { scolaritéValidationSchema } from "./ScolaritéForm.validation";
import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { i18n } from "@/configuration/i18n/i18n";
import { bacsQueryOptions, spécialitésPourUnBacQueryOptions } from "@/features/bac/ui/options";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { useEffect } from "react";

const ScolaritéForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ScolaritéFormProps) => {
  const CHAMP_SPÉCIALITÉS = "spécialités";
  const { data: bacs } = useSuspenseQuery(bacsQueryOptions);

  const { register, watch, getValues, setValue, erreurs, mettreÀJourÉlève, dirtyFields } = useÉlèveForm({
    schémaValidation: scolaritéValidationSchema(bacs),
    àLaSoumissionDuFormulaireAvecSuccès,
  });
  const valeurBac = watch("bac");

  const { data: spécialités, refetch: récupérerSpécialitésPourUnBac } = useQuery(
    spécialitésPourUnBacQueryOptions(valeurBac),
  );

  const classeOptions: ClasseOptions = [
    {
      valeur: "seconde",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.SECONDE.LABEL,
    },
    {
      valeur: "seconde_sthr",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.SECONDE_STHR.LABEL,
    },
    {
      valeur: "seconde_tmd",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.SECONDE_TMD.LABEL,
    },
    {
      valeur: "première",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.PREMIÈRE.LABEL,
    },
    {
      valeur: "terminale",
      label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.TERMINALE.LABEL,
    },
  ];

  const bacOptions = bacs?.map((bac) => ({ valeur: bac.id, label: bac.nom }));
  const spécialitéOptions = spécialités?.map((spécialité) => ({ valeur: spécialité.id, label: spécialité.nom }));

  useEffect(() => {
    if (dirtyFields.bac) {
      setValue(CHAMP_SPÉCIALITÉS, []);
    }

    récupérerSpécialitésPourUnBac();
  }, [dirtyFields.bac, récupérerSpécialitésPourUnBac, setValue, valeurBac]);

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      <div className="grid grid-flow-row gap-8 md:grid-flow-col">
        <ListeDéroulante
          label={i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LABEL}
          options={classeOptions}
          registerHookForm={register("classe")}
          status={erreurs.classe ? { type: "erreur", message: erreurs.classe.message } : undefined}
        />
        <ListeDéroulante
          label={i18n.ÉLÈVE.SCOLARITÉ.BAC.LABEL}
          options={bacOptions ?? []}
          registerHookForm={register("bac")}
          status={erreurs.bac ? { type: "erreur", message: erreurs.bac.message } : undefined}
        />
      </div>

      {spécialités && spécialités.length > 0 && (
        <div className="mt-12">
          <SélecteurMultiple
            auChangementOptionsSélectionnées={(valeursOptionsSélectionnées) =>
              setValue(CHAMP_SPÉCIALITÉS, valeursOptionsSélectionnées)
            }
            description={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.DESCRIPTION}
            key={valeurBac}
            label={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL}
            options={spécialitéOptions ?? []}
            texteOptionsSélectionnées={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS_SÉLECTIONNÉES}
            valeursOptionsSélectionnéesParDéfaut={getValues(CHAMP_SPÉCIALITÉS)}
          />
        </div>
      )}
    </form>
  );
};

export default ScolaritéForm;
