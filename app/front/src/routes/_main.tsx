import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { type QueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const chargerDonnées = async (queryClient: QueryClient) => {
  await queryClient.ensureQueryData(référentielDonnéesQueryOptions);
};

const tableauDeBordSearchSchema = z.object({
  associationPS: z.enum(["ok", "erreur"]).optional(),
});

export const Route = createFileRoute("/_main")({
  validateSearch: (searchParamètres) => tableauDeBordSearchSchema.parse(searchParamètres),
  component: MainLayout,
  loader: async ({ context: { queryClient } }) => {
    await chargerDonnées(queryClient);
  },
});
