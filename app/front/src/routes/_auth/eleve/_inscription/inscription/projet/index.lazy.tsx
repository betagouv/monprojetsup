import ÉlèveInscriptionPage from "@/features/élève/ui/inscription/ÉlèveInscriptionPage/ÉlèveInscriptionPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/eleve/_inscription/inscription/projet/")({
  component: ÉlèveInscriptionPage,
});
