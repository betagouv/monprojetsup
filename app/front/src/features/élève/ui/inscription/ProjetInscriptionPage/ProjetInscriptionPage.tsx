import Head from "@/components/_layout/Head/Head";
import { i18n } from "@/configuration/i18n/i18n";
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

  const àLaSoumissionDuFormulaireAvecSuccès = () => {
    navigate({ to: étapeSuivante?.url });
  };

  return (
    <>
      <Head title={i18n.ÉLÈVE.PROJET.PARCOURS_INSCRIPTION.TITRE_ÉTAPE} />
      <MonProjetForm
        formId={étapeActuelle?.url ?? ""}
        valeursParDéfaut={élève}
        àLaSoumissionDuFormulaireAvecSuccès={àLaSoumissionDuFormulaireAvecSuccès}
      />
    </>
  );
};

export default ProjetInscriptionPage;
