import Head from "@/components/_layout/Head/Head";
import { dépendances } from "@/configuration/dépendances/dépendances";
import { i18n } from "@/configuration/i18n/i18n";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { type Élève } from "@/features/élève/domain/élève.interface";
import {
  étapeActuelleÉtapesInscriptionÉlèveStore,
  étapeSuivanteÉtapesInscriptionÉlèveStore,
} from "@/features/élève/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
import MonProjetForm from "@/features/élève/ui/formulaires/ProjetForm/ProjetForm";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import { useMutation, useSuspenseQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";

const ProjetInscriptionPage = () => {
  const { data: élève } = useSuspenseQuery(élèveQueryOptions);

  const navigate = useNavigate();
  const étapeSuivante = étapeSuivanteÉtapesInscriptionÉlèveStore();
  const étapeActuelle = étapeActuelleÉtapesInscriptionÉlèveStore();

  const mutationÉlève = useMutation({
    mutationFn: async (données: Partial<Omit<Élève, "id">>) => {
      return await dépendances.mettreÀJourÉlèveUseCase.run(données);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["élève"] });
      navigate({ to: étapeSuivante?.url });
    },
  });

  return (
    <>
      <Head title={i18n.ÉLÈVE.PROJET.PARCOURS_INSCRIPTION.TITRE_ÉTAPE} />
      <MonProjetForm
        formId={étapeActuelle?.url ?? ""}
        valeursParDéfaut={élève}
        àLaSoumissionDuFormulaireSansErreur={(données) => mutationÉlève.mutateAsync(données)}
      />
    </>
  );
};

export default ProjetInscriptionPage;
