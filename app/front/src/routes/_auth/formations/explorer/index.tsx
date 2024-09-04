import { suggérerFormationsQueryOptions } from "@/features/formation/ui/formationQueries";
import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/formations/explorer/")({
  loader: async ({ context: { queryClient } }) => {
    const suggestions = await queryClient.ensureQueryData(suggérerFormationsQueryOptions);

    throw redirect({
      to: "/formations/$formationId",
      params: { formationId: suggestions[0].id },
    });
  },
});
