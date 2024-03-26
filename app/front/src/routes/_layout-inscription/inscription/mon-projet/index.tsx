import InscriptionMonProjet from "@/features/inscriptionÉlève/ui/InscriptionMonProjet/InscriptionMonProjet";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_layout-inscription/inscription/mon-projet/")({
  component: InscriptionMonProjet,
});
