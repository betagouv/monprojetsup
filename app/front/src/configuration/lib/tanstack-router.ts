import { userManagerOIDCClient } from "./oidc-client";
import { queryClient } from "./tanstack-query";
import { routeTree } from "@/routeTree.gen";
import { createRouter } from "@tanstack/react-router";

export const router = createRouter({
  routeTree,
  context: {
    queryClient,
    auth: userManagerOIDCClient,
  },
  defaultPreload: false,
  defaultPreloadStaleTime: 0,
  scrollRestoration: true,
});

declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}
