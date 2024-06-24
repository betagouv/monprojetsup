import { détailFormationQueryOptions } from "@/features/formation/ui/options";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/formations/$formationId/")({
  loader: async ({ params, context: { queryClient } }) => {
    await queryClient.ensureQueryData(détailFormationQueryOptions(params.formationId));
  },
});
