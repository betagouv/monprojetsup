import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { type QueryClient } from "@tanstack/react-query";
import { createRootRouteWithContext } from "@tanstack/react-router";

export const Route = createRootRouteWithContext<{ queryClient: QueryClient }>()({ component: MainLayout });
