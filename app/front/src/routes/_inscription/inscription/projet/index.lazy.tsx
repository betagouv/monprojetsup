import ProjetInscriptionPage from "@/features/élève/ui/inscription/ProjetInscriptionPage/ProjetInscriptionPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_inscription/inscription/projet/")({
  component: ProjetInscriptionPage,
});
