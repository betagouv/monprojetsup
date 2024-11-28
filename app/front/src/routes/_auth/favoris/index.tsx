import { useListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import FavorisPage from "@/features/élève/ui/favoris/FavorisPage/FavorisPage";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/favoris/")({
  component: FavorisPage,
  loader: ({ context: { queryClient }, cause }) => {
    if (cause !== "stay") {
      const listeEtAperçuStore = useListeEtAperçuStore.getState();
      listeEtAperçuStore.actions.réinitialiserStore();

      queryClient.removeQueries({ queryKey: ["formations"] });
      queryClient.removeQueries({ queryKey: ["métiers"] });
    }

    return queryClient.fetchQuery(élèveQueryOptions);
  },
});
