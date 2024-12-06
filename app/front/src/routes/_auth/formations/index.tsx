import { useListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import FormationPage from "@/features/formation/ui/FormationPage/FormationPage";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/formations/")({
  component: FormationPage,
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
