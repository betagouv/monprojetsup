import { type QueryClient } from "@tanstack/react-query";
import { createRootRouteWithContext } from "@tanstack/react-router";
import { type UserManager } from "oidc-client-ts";

interface RouterContext {
  queryClient: QueryClient;
  auth: UserManager;
}

export const Route = createRootRouteWithContext<RouterContext>()();
