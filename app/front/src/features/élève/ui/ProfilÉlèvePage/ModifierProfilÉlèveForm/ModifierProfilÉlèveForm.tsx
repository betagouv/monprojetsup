import { type ModifierProfilÉlèveFormProps } from "./ModifierProfilÉlèveForm.interface";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre";
import { actionsToastStore } from "@/components/Toast/useToastStore/useToastStore";
import { i18n } from "@/configuration/i18n/i18n";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import ÉtudeForm from "@/features/élève/ui/formulaires/ÉtudeForm/ÉtudeForm";
import IntérêtsForm from "@/features/élève/ui/formulaires/IntérêtsForm/IntérêtsForm";
import MétiersForm from "@/features/élève/ui/formulaires/MétiersForm/MétiersForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import FormationsMasquées from "@/features/élève/ui/ProfilÉlèvePage/FormationsMasquées/FormationsMasquées";
import { Suspense } from "react";

const ModifierProfilÉlèveForm = ({ formulaireId, titre }: ModifierProfilÉlèveFormProps) => {
  const { déclencherToast, fermerToast } = actionsToastStore();

  const àLaSoumissionDuFormulaireAvecSuccès = () => {
    déclencherToast("", i18n.COMMUN.MODIFICATIONS_ENREGISTRÉES, "success");
  };

  const formulaireÀAfficher = () => {
    const propsFormulaire = {
      àLaSoumissionDuFormulaireAvecSuccès,
    };

    switch (formulaireId) {
      case "scolarité":
        return (
          <ScolaritéForm
            formId={formulaireId}
            {...propsFormulaire}
          />
        );
      case "domaines":
        return (
          <DomainesForm
            formId={formulaireId}
            niveauDeTitreCatégories="h3"
            {...propsFormulaire}
          />
        );
      case "intérêts":
        return (
          <IntérêtsForm
            formId={formulaireId}
            niveauDeTitreCatégories="h3"
            {...propsFormulaire}
          />
        );
      case "étude":
        return (
          <>
            <ÉtudeForm
              formId={formulaireId}
              {...propsFormulaire}
            />
            <hr className="mb-10 mt-14 pb-[1px]" />
            <FormationsMasquées />
          </>
        );
      case "métiers":
        return (
          <MétiersForm
            formId={formulaireId}
            {...propsFormulaire}
          />
        );
      default:
        return null;
    }
  };

  return (
    <Suspense fallback={<AnimationChargement />}>
      <div className="*:mb-10">
        <Titre
          niveauDeTitre="h2"
          styleDeTitre="h4"
        >
          {titre}
        </Titre>
      </div>
      {formulaireÀAfficher()}
      <hr className="mt-12" />
      <div className="fr-grid-row justify-end">
        <Bouton
          auClic={fermerToast}
          formId={formulaireId}
          type="submit"
        >
          {i18n.COMMUN.ENREGISTRER}
        </Bouton>
      </div>
    </Suspense>
  );
};

export default ModifierProfilÉlèveForm;
