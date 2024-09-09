import { type ScolaritéFormProps } from "./ScolaritéForm.interface";
import useScolaritéForm from "./useScolaritéForm";
import ListeDéroulante from "@/components/_dsfr/ListeDéroulante/ListeDéroulante";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { i18n } from "@/configuration/i18n/i18n";

const ScolaritéForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ScolaritéFormProps) => {
  const {
    mettreÀJourÉlève,
    erreurs,
    register,
    classeOptions,
    bacOptions,
    valeurBac,
    bacADesSpécialités,
    spécialitésSuggérées,
    spécialitésSélectionnéesParDéfaut,
    auChangementDesSpécialitésSélectionnées,
    àLaRechercheDUneSpécialité,
  } = useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès });

  return (
    <form
      id={formId}
      noValidate
      onSubmit={mettreÀJourÉlève}
    >
      <div className="grid grid-flow-row gap-8 md:grid-cols-[1fr_1fr]">
        <ListeDéroulante
          label={i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LABEL}
          obligatoire
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
      {bacADesSpécialités && spécialitésSélectionnéesParDéfaut && (
        <div className="mt-12">
          <SélecteurMultiple
            auChangementOptionsSélectionnées={auChangementDesSpécialitésSélectionnées}
            description={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.DESCRIPTION}
            key={`${valeurBac}${spécialitésSélectionnéesParDéfaut.length}`}
            label={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL}
            nombreDeCaractèreMinimumRecherche={2}
            optionsSuggérées={spécialitésSuggérées}
            optionsSélectionnéesParDéfaut={spécialitésSélectionnéesParDéfaut}
            rechercheSuggestionsEnCours={false}
            texteOptionsSélectionnées={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.SÉLECTIONNÉS}
            àLaRechercheDUneOption={àLaRechercheDUneSpécialité}
          />
        </div>
      )}
    </form>
  );
};

export default ScolaritéForm;
