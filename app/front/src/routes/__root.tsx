import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { type QueryClient } from "@tanstack/react-query";
import { createRootRouteWithContext } from "@tanstack/react-router";

interface RouterContext {
  queryClient: QueryClient;
}

export const Route = createRootRouteWithContext<RouterContext>()({ component: MainLayout });
