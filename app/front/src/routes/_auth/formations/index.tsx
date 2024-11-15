import { useListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import DétailFormationPage from "@/features/formation/ui/DétailFormationPage/DétailFormationPage";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/formations/")({
  component: DétailFormationPage,
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
