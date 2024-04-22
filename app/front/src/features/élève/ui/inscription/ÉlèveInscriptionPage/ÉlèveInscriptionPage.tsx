import Head from "@/components/_layout/Head/Head";
import {
  étapeActuelleÉtapesInscriptionÉlèveStore,
  étapeSuivanteÉtapesInscriptionÉlèveStore,
} from "@/features/élève/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import ProjetForm from "@/features/élève/ui/formulaires/ProjetForm/ProjetForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import { useSuspenseQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";

const ÉlèveInscriptionPage = () => {
  useSuspenseQuery(élèveQueryOptions);

  const navigate = useNavigate();
  const étapeSuivante = étapeSuivanteÉtapesInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleÉtapesInscriptionÉlèveStore();

  const àLaSoumissionDuFormulaireAvecSuccès = () => {
    navigate({ to: étapeSuivante?.url });
  };

  if (!étapeActuelle) return null;

  const propsFormulaire = {
    formId: étapeActuelle?.url ?? "",
    àLaSoumissionDuFormulaireAvecSuccès,
  };

  const formulaireÀAfficher = () => {
    switch (étapeActuelle?.url) {
      case "/inscription/projet":
        return <ProjetForm {...propsFormulaire} />;
      case "/inscription/scolarite":
        return <ScolaritéForm {...propsFormulaire} />;
      case "/inscription/domaines":
        return <DomainesForm />;
      default:
        return null;
    }
  };

  return (
    <>
      <Head title={étapeActuelle.titreÉtape} />
      {formulaireÀAfficher()}
    </>
  );
};

export default ÉlèveInscriptionPage;
