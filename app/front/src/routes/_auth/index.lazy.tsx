import TableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/TableauDeBordÉlève/TableauDeBordÉlève";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/")({
  component: TableauDeBordÉlève,
});
