import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { env } from "@/configuration/environnement";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { createFileRoute } from "@tanstack/react-router";
import { withAuthenticationRequired } from "react-oidc-context";

export const Route = createFileRoute("/_auth")({
  component: env.VITE_TEST_MODE ? MainLayout : withAuthenticationRequired(MainLayout),
  loader: async ({ context: { queryClient } }) => {
    await queryClient.ensureQueryData(référentielDonnéesQueryOptions);
    await queryClient.ensureQueryData(élèveQueryOptions);
  },
});
