import { useListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import FavorisÉlèvePage from "@/features/élève/ui/FavorisÉlèvePage/FavorisÉlèvePage";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/favoris/")({
  component: FavorisÉlèvePage,
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
