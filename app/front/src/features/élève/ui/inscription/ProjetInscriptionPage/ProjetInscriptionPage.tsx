import { queryClient } from "@/configuration/lib/tanstack-query";
import {
  étapeActuelleÉtapesInscriptionÉlèveStore,
  étapeSuivanteÉtapesInscriptionÉlèveStore,
} from "@/features/élève/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
import MonProjetForm from "@/features/élève/ui/formulaires/ProjetForm/ProjetForm";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import { useSuspenseQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";

const ProjetInscriptionPage = () => {
  const { data: élève } = useSuspenseQuery(élèveQueryOptions);

  const navigate = useNavigate();
  const étapeSuivante = étapeSuivanteÉtapesInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleÉtapesInscriptionÉlèveStore();

  const allerÀÉtapeSuivante = () => {
    queryClient.invalidateQueries({ queryKey: ["élève"] });
    navigate({ to: étapeSuivante?.url });
  };

  return (
    <MonProjetForm
      formId={étapeActuelle?.url ?? ""}
      valeursParDéfaut={élève}
      àLaSoumissionDuFormulaireAvecSuccès={allerÀÉtapeSuivante}
    />
  );
};

export default ProjetInscriptionPage;
