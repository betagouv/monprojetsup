import RecherchePage from "@/features/recherche/ui/RecherchePage/RecherchePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/recherche/")({
  component: RecherchePage,
});
