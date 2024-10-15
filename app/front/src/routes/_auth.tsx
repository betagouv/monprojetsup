import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { environnement } from "@/configuration/environnement";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { type QueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { withAuthenticationRequired } from "react-oidc-context";

const chargerDonnées = async (queryClient: QueryClient) => {
  await queryClient.ensureQueryData(référentielDonnéesQueryOptions);
  await queryClient.ensureQueryData(élèveQueryOptions);
};

export const Route = createFileRoute("/_auth")({
  component: environnement.VITE_TEST_MODE ? MainLayout : withAuthenticationRequired(MainLayout),
  loader: async ({ context: { queryClient, auth } }) => {
    const user = await auth.getUser();

    if (user) {
      await chargerDonnées(queryClient);
    } else {
      auth.events.addUserLoaded(async () => await chargerDonnées(queryClient));
    }
  },
});
