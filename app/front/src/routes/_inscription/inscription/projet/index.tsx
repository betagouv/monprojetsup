import MonProjetInscription from "@/features/inscriptionÉlève/ui/MonProjetInscription/MonProjetInscription";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_inscription/inscription/projet/")({
  component: MonProjetInscription,
});
