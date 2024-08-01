import { env } from "@/configuration/environnement";
import { createFileRoute, Outlet } from "@tanstack/react-router";
import { withAuthenticationRequired } from "react-oidc-context";

export const Route = createFileRoute("/_auth")({
  component: env.VITE_TEST_MODE ? Outlet : withAuthenticationRequired(Outlet),
});
