import { type ScolaritéFormProps } from "./ScolaritéForm.interface";
import useScolaritéForm from "./useScolaritéForm";
import CurseurCranté from "@/components/CurseurCranté/CurseurCranté";
import SélecteurMultiple from "@/components/SélecteurMultiple/SélecteurMultiple";
import { constantes } from "@/configuration/constantes";
import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n";
import { Select } from "@codegouvfr/react-dsfr/SelectNext";

const ScolaritéForm = ({ àLaSoumissionDuFormulaireAvecSuccès, formId }: ScolaritéFormProps) => {
  const {
    mettreÀJourÉlève,
    erreurs,
    register,
    classeOptions,
    bacOptions,
    valeurBac,
    afficherChampMoyenne,
    neVeutPasRépondreMoyenne,
    moyenneGénérale,
    auClicSurNeVeutPasRépondreMoyenne,
    pourcentageAdmisAyantCetteMoyenneOuMoins,
    bacADesSpécialités,
    spécialitésSuggérées,
    spécialitésSélectionnéesParDéfaut,
    auChangementDesSpécialitésSélectionnées,
    àLaRechercheDUneSpécialité,
  } = useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès });

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
      {environnement.VITE_FF_MOYENNE_GENERALE && afficherChampMoyenne && (
        <div>
          <CurseurCranté
            auClicSurNeVeutPasRépondre={auClicSurNeVeutPasRépondreMoyenne}
            description={i18n.ÉLÈVE.SCOLARITÉ.MOYENNE.DESCRIPTION}
            key={neVeutPasRépondreMoyenne.toString()}
            label={i18n.ÉLÈVE.SCOLARITÉ.MOYENNE.LABEL}
            neVeutPasRépondre={neVeutPasRépondreMoyenne}
            registerHookForm={register("moyenneGénérale", {
              valueAsNumber: true,
            })}
            status={erreurs.moyenneGénérale ? { type: "erreur", message: erreurs.moyenneGénérale.message } : undefined}
            valeurMax={20}
            valeurMin={0}
            valeurParDéfaut={moyenneGénérale}
          />
          {pourcentageAdmisAyantCetteMoyenneOuMoins !== undefined && pourcentageAdmisAyantCetteMoyenneOuMoins >= 0 && (
            <div className="fr-alert fr-alert--info fr-alert--sm mt-6">
              <p>
                {i18n.ÉLÈVE.SCOLARITÉ.MOYENNE.AUTO_CENSURE} {pourcentageAdmisAyantCetteMoyenneOuMoins}{" "}
                {i18n.ÉLÈVE.SCOLARITÉ.MOYENNE.AUTO_CENSURE_SUITE}{" "}
                {bacOptions.find((bacOption) => valeurBac === bacOption.value)?.label}{" "}
                {i18n.ÉLÈVE.SCOLARITÉ.MOYENNE.AUTO_CENSURE_SUITE_2} {moyenneGénérale}{" "}
                {i18n.ÉLÈVE.SCOLARITÉ.MOYENNE.AUTO_CENSURE_FIN}
              </p>
            </div>
          )}
        </div>
      )}
      {bacADesSpécialités && spécialitésSélectionnéesParDéfaut && (
        <div>
          <SélecteurMultiple
            auChangementOptionsSélectionnées={auChangementDesSpécialitésSélectionnées}
            description={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.DESCRIPTION}
            key={`${valeurBac}${spécialitésSélectionnéesParDéfaut.length}`}
            label={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL}
            nombreDeCaractèreMinimumRecherche={constantes.SPÉCIALITÉS.NB_CARACTÈRES_MIN_RECHERCHE}
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
