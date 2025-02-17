import InscriptionÉlèvePage from "@/features/élève/ui/ParcoursInscriptionÉlève/InscriptionÉlèvePage/InscriptionÉlèvePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_main/eleve/_inscription/inscription/formations/")({
  component: InscriptionÉlèvePage,
});
