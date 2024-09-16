import DétailFormationPage from "@/features/formation/ui/DétailFormationPage/DétailFormationPage";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const ficheFormationSearchSchema = z.object({
  recherche: z.string().optional(),
});

export const Route = createFileRoute("/_auth/formations/")({
  component: DétailFormationPage,
  validateSearch: (searchParamètres) => ficheFormationSearchSchema.parse(searchParamètres),
  loader: async ({ context: { queryClient } }) => {
    queryClient.removeQueries({ queryKey: ["formationsSuggestions"] });
    queryClient.removeQueries({ queryKey: ["formations"] });
  },
});
