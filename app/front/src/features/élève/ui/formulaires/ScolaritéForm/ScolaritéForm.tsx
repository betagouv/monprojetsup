import { type ClasseOptions, type ScolaritéFormProps } from "./ScolaritéForm.interface";
import { scolaritéValidationSchema } from "./ScolaritéForm.validation";
import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

const ScolaritéForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ScolaritéFormProps) => {
  const { register, erreurs, mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: scolaritéValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

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

  return (
    <form
      id={formId}
      onSubmit={mettreÀJourÉlève}
    >
      <ListeDéroulante
        label={i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LÉGENDE}
        options={classeOptions}
        registerHookForm={register("classe")}
        status={erreurs.classe ? { type: "erreur", message: erreurs.classe.message } : undefined}
      />
    </form>
  );
};

export default ScolaritéForm;

// const { data: spécialités } = useQuery(spécialitésPourUnBacQueryOptions("Générale"));
// {spécialités && (
//   <SélecteurMultiple
//     auChangementOptionsSélectionnées={(valeursOptionsSélectionnées) => console.log(valeursOptionsSélectionnées)}
//     description="Commence à taper puis sélectionne des enseignements"
//     label="Enseignements de spécialité (EDS) choisis ou envisagés"
//     options={spécialités.map((spécialité) => ({ valeur: spécialité.id, label: spécialité.nom }))}
//     texteOptionsSélectionnées="Enseignement(s) de spécialité sélectionné(s)"
//     valeursOptionsSélectionnéesParDéfaut={["1061", "1063"]}
//   />
// )}
