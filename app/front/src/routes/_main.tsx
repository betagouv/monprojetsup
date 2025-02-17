import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { type QueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";

const chargerDonnées = async (queryClient: QueryClient) => {
  await queryClient.ensureQueryData(référentielDonnéesQueryOptions);
};

export const Route = createFileRoute("/_main")({
  component: MainLayout,
  loader: async ({ context: { queryClient } }) => {
    await chargerDonnées(queryClient);
  },
});
