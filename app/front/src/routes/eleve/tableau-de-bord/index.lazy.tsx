import TableauDeBordÉlèvePage from "@/features/élève/ui/tableauDeBord/TableauDeBordÉlèvePage/TableauDeBordÉlèvePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/eleve/tableau-de-bord/")({
  component: TableauDeBordÉlèvePage,
});
