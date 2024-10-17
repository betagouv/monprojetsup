import { createLazyFileRoute, useRouterState } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/parcoursup-callback/")({
  component: () => {
    const router = useRouterState();
    return <>{JSON.stringify(router.location.search)}</>;
  },
});
