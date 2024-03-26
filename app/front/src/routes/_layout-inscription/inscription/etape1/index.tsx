import AccueilPage from "@/features/accueil/ui/AccueilPage/AccueilPage";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_layout-inscription/inscription/etape1/")({ component: AccueilPage });
