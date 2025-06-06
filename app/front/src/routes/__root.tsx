import Erreur from "@/components/Erreur/Erreur";
import { dépendances } from "@/configuration/dépendances/dépendances";
import { router } from "@/configuration/lib/tanstack-router.ts";
import { type QueryClient } from "@tanstack/react-query";
import { createRootRouteWithContext } from "@tanstack/react-router";
import { type UserManager } from "oidc-client-ts";

interface RouterContext {
  queryClient: QueryClient;
  auth: UserManager;
}

export const Route = createRootRouteWithContext<RouterContext>()({
  errorComponent: ({ error }) => <Erreur erreur={error} />,
  beforeLoad: () => {
    const estUrlDeRedirectionLogin = /session_state=/u.test(router.latestLocation.searchStr);
    if (!estUrlDeRedirectionLogin) {
      dépendances.analyticsRepository.envoyerPageVue(router.latestLocation.href);
    }
  },
});
