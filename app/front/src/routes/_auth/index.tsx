import { queryÉlèveKeys } from "@/features/élève/ui/élèveQueries";
import TableauDeBordÉlèvePage from "@/features/élève/ui/TableauDeBordÉlèvePage/TableauDeBordÉlèvePage";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const tableauDeBordSearchSchema = z.object({
  associationPS: z.enum(["ok", "erreur"]).optional(),
});

export const Route = createFileRoute("/_auth/")({
  validateSearch: (searchParamètres) => tableauDeBordSearchSchema.parse(searchParamètres),
  component: TableauDeBordÉlèvePage,
  loader: ({ context: { queryClient }, cause }) => {
    if (cause !== "stay") {
      queryClient.removeQueries({ queryKey: [queryÉlèveKeys.PROGRESSION] });
    }
  },
});
