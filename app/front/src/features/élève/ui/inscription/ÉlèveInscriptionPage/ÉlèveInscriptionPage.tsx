import Head from "@/components/_layout/Head/Head";
import DomainesForm from "@/features/élève/ui/formulaires/DomainesForm/DomainesForm";
import ÉtudeForm from "@/features/élève/ui/formulaires/ÉtudeForm/ÉtudeForm";
import FormationsForm from "@/features/élève/ui/formulaires/FormationsForm/FormationsForm";
import IntêretsForm from "@/features/élève/ui/formulaires/IntêretsForm/IntêretsForm";
import MétiersForm from "@/features/élève/ui/formulaires/MétiersForm/MétiersForm";
import ProjetForm from "@/features/élève/ui/formulaires/ProjetForm/ProjetForm";
import ScolaritéForm from "@/features/élève/ui/formulaires/ScolaritéForm/ScolaritéForm";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import {
  étapeActuelleÉtapesInscriptionÉlèveStore,
  étapeSuivanteÉtapesInscriptionÉlèveStore,
} from "@/features/élève/ui/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
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

  const composantÀAfficher = () => {
    switch (étapeActuelle?.url) {
      case "/inscription/projet":
        return <ProjetForm {...propsFormulaire} />;
      case "/inscription/scolarite":
        return <ScolaritéForm {...propsFormulaire} />;
      case "/inscription/domaines":
        return <DomainesForm {...propsFormulaire} />;
      case "/inscription/interets":
        return <IntêretsForm {...propsFormulaire} />;
      case "/inscription/metiers":
        return <MétiersForm {...propsFormulaire} />;
      case "/inscription/etude":
        return <ÉtudeForm {...propsFormulaire} />;
      case "/inscription/formations":
        return <FormationsForm {...propsFormulaire} />;
      case "/inscription/confirmation":
        return "Confirmation";
      default:
        return null;
    }
  };

  return (
    <>
      <Head title={étapeActuelle.titreÉtape} />
      {composantÀAfficher()}
    </>
  );
};

export default ÉlèveInscriptionPage;
