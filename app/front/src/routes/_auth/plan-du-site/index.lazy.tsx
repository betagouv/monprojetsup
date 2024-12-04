import PlanDuSitePage from "@/features/pages-pied-de-page/ui/PlanDuSitePage/PlanDuSitePage.tsx";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/plan-du-site/")({
  component: PlanDuSitePage,
});
