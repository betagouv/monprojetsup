import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { queryÉlèveKeys } from "@/features/élève/ui/élèveQueries";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { type QueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";

const chargerDonnées = async (queryClient: QueryClient) => {
  await queryClient.ensureQueryData(référentielDonnéesQueryOptions);
};

export const Route = createFileRoute("/_main/_main")({
  component: MainLayout,
  loader: async ({ context: { queryClient }, cause }) => {
    await chargerDonnées(queryClient);
    if (cause !== "stay") {
      queryClient.removeQueries({ queryKey: [queryÉlèveKeys.PROGRESSION] });
    }
  },
});
