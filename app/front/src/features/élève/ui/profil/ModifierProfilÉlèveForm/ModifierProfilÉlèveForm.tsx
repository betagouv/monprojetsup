import { type ModifierProfilÉlèveFormProps } from "./ModifierProfilÉlèveForm.interface";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import ÉtudeForm from "@/features/élève/ui/formulaires/ÉtudeForm/ÉtudeForm";
import IntêretsForm from "@/features/élève/ui/formulaires/IntêretsForm/IntêretsForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import { Suspense, useState } from "react";

const ModifierProfilÉlèveForm = ({ formulaireId, titre }: ModifierProfilÉlèveFormProps) => {
  const [statusMessage, setStatusMessage] = useState<string | undefined>();

  const àLaSoumissionDuFormulaireAvecSuccès = () => {
    setStatusMessage(i18n.COMMUN.MODIFICATIONS_ENREGISTRÉES);
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
      case "intêrets":
        return (
          <IntêretsForm
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
      <div aria-live="assertive">
        {statusMessage && (
          <div className="fr-alert fr-alert--success fr-alert--sm mt-12">
            <p>{statusMessage}</p>
          </div>
        )}
      </div>
      <hr className="mt-12" />
      <div className="fr-grid-row justify-end">
        <Bouton
          auClic={() => setStatusMessage(undefined)}
          formId={formulaireId}
          label={i18n.COMMUN.ENREGISTRER}
          type="submit"
        />
      </div>
    </Suspense>
  );
};

export default ModifierProfilÉlèveForm;
