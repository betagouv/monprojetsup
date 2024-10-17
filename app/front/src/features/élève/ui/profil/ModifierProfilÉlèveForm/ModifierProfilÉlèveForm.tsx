import { type ModifierProfilÉlèveFormProps } from "./ModifierProfilÉlèveForm.interface";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre";
import { actionsToastStore } from "@/components/Toast/useToast/useToast";
import { i18n } from "@/configuration/i18n/i18n";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import IntérêtsForm from "@/features/élève/ui/formulaires/IntérêtsForm/IntérêtsForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import ÉtudeForm from "@/features/élève/ui/formulaires/ÉtudeForm/ÉtudeForm";
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
          <ÉtudeForm
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
          label={i18n.COMMUN.ENREGISTRER}
          type="submit"
        />
      </div>
    </Suspense>
  );
};

export default ModifierProfilÉlèveForm;
