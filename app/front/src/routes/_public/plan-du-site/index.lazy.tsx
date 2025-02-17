import PlanDuSitePage from "@/features/pagesObligatoires/ui/PlanDuSitePage/PlanDuSitePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_public/plan-du-site/")({
  component: PlanDuSitePage,
});
