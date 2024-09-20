import ConfirmationInscriptionÉlève from "@/features/élève/ui/inscription/ConfirmationInscriptionÉlève/ConfirmationInscriptionÉlève";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/eleve/_inscription/inscription/confirmation/")({
  component: ConfirmationInscriptionÉlève,
});
