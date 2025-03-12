import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { environnement } from "@/configuration/environnement";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { MpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient";
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
    if (!environnement.VITE_TEST_MODE) {
      try {
        const user = await auth.signinSilent();
        if (user === null) {
          MpsApiHttpClient.setNonAuthentifié();
          await auth.removeUser();
        }
      } catch {
        await auth.removeUser();
      }
    }

    await queryClient.invalidateQueries(élèveQueryOptions);
    await chargerDonnées(queryClient);
  },
});
