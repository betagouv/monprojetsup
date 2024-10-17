import TableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/TableauDeBordÉlève/TableauDeBordÉlève";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const tableauDeBordSearchSchema = z.object({
  associationPS: z.enum(["ok", "erreur"]).optional(),
});

export const Route = createFileRoute("/_auth/")({
  validateSearch: (searchParamètres) => tableauDeBordSearchSchema.parse(searchParamètres),
  component: TableauDeBordÉlève,
});
