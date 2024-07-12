import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { type QueryClient } from "@tanstack/react-query";
import { createRootRouteWithContext } from "@tanstack/react-router";
import { type AuthContextProps } from "react-oidc-context";

interface RouterContext {
  queryClient: QueryClient;
  auth: AuthContextProps;
}

export const Route = createRootRouteWithContext<RouterContext>()({ component: MainLayout });
