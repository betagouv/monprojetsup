import DétailFormationPage from "@/features/formation/ui/DétailFormationPage/DétailFormationPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/formations/$formationId/")({
  component: DétailFormationPage,
});
