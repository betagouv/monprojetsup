import AccueilPage from "@/features/accueil/ui/AccueilPage/AccueilPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/")({ component: AccueilPage });
