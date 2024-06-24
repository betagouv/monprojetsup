import LayoutInscription from "@/features/élève/ui/inscription/LayoutInscription/LayoutInscription";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/eleve/_inscription")({ component: LayoutInscription });
