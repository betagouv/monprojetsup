import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/eleve/_inscription/inscription/projet/")({
  loader: async ({ context: { queryClient } }) => await queryClient.ensureQueryData(élèveQueryOptions),
});
