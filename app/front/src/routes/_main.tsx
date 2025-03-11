import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { type QueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const chargerDonnées = async (queryClient: QueryClient) => {
  await queryClient.ensureQueryData(référentielDonnéesQueryOptions);
  await queryClient.ensureQueryData(élèveQueryOptions);
};

const tableauDeBordSearchSchema = z.object({
  associationPS: z.enum(["ok", "erreur"]).optional(),
});

export const Route = createFileRoute("/_main")({
  validateSearch: (searchParamètres) => tableauDeBordSearchSchema.parse(searchParamètres),
  component: MainLayout,
  loader: async ({ context: { queryClient, auth } }) => {
    await chargerDonnées(queryClient);
    auth.events.addUserLoaded(async () => {
      await queryClient.refetchQueries(élèveQueryOptions);
      window.location.href = "/";
    });
  },
});
