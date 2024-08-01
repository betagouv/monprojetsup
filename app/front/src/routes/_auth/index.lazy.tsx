import TableauDeBordPage from "@/components/_pages/TableauDeBordPage/TableauDeBordPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/")({
  component: TableauDeBordPage,
});
