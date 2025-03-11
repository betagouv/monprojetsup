import MainLayout from "@/components/_layout/MainLayout/MainLayout";
import { environnement } from "@/configuration/environnement";
import { createFileRoute } from "@tanstack/react-router";
import { withAuthenticationRequired } from "react-oidc-context";

export const Route = createFileRoute("/connexion/")({
  component: environnement.VITE_TEST_MODE ? MainLayout : withAuthenticationRequired(MainLayout),
});
