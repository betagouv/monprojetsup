import MonProjetForm from "@/features/élève/ui/MonProjetForm/MonProjetForm";
import {
  étapeActuelleInscriptionÉlèveStore,
  étapeSuivanteInscriptionÉlèveStore,
} from "@/store/useInscriptionÉlève/useInscriptionÉlève";
import { useNavigate } from "@tanstack/react-router";

const MonProjetInscription = () => {
  const navigate = useNavigate();
  const étapeSuivante = étapeSuivanteInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleInscriptionÉlèveStore();

  const allerÀÉtapeSuivante = () => {
    navigate({ to: étapeSuivante?.url });
  };

  return (
    <MonProjetForm
      formId={étapeActuelle?.url ?? ""}
      àLaSoumissionDuFormulaireAvecSuccès={allerÀÉtapeSuivante}
    />
  );
};

export default MonProjetInscription;
