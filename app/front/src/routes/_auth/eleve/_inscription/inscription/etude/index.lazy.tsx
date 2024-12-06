import InscriptionÉlèvePage from "@/features/élève/ui/ParcoursInscriptionÉlève/InscriptionÉlèvePage/InscriptionÉlèvePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/eleve/_inscription/inscription/etude/")({
  component: InscriptionÉlèvePage,
});
