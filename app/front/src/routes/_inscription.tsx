import LayoutInscription from "@/features/inscriptionÉlève/ui/LayoutInscription/LayoutInscription";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_inscription")({ component: LayoutInscription });
