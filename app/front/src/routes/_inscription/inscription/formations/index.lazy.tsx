import ÉlèveInscriptionPage from "@/features/élève/ui/inscription/ÉlèveInscriptionPage/ÉlèveInscriptionPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_inscription/inscription/formations/")({
  component: ÉlèveInscriptionPage,
});
