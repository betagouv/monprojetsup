import ÉlèveInscriptionPage from "@/features/élève/ui/inscription/ÉlèveInscriptionPage/ÉlèveInscriptionPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/eleve/_inscription/inscription/scolarite/")({
  component: ÉlèveInscriptionPage,
});
