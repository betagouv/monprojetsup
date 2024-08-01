import LayoutInscription from "@/features/élève/ui/inscription/LayoutInscription/LayoutInscription";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/eleve/_inscription")({ component: LayoutInscription });
