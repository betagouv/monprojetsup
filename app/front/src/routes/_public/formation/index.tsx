import { useListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import FormationSansRecherchePage from "@/features/formation/ui/FormationPage/FormationSansRecherchePage";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_public/formation/")({
  component: FormationSansRecherchePage,
  loader: ({ context: { queryClient }, cause }) => {
    if (cause !== "stay") {
      const listeEtAperçuStore = useListeEtAperçuStore.getState();
      listeEtAperçuStore.actions.réinitialiserStore();

      queryClient.removeQueries({ queryKey: ["métiers"] });
      queryClient.removeQueries({ queryKey: ["formationsSuggestions"] });
      queryClient.removeQueries({ queryKey: ["formations"] });
    }
  },
});
