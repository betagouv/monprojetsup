import ÉlèveInscriptionPage from "@/features/élève/ui/inscription/InscriptionÉlèvePage/InscriptionÉlèvePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/eleve/_inscription/inscription/formations/")({
  component: ÉlèveInscriptionPage,
});
