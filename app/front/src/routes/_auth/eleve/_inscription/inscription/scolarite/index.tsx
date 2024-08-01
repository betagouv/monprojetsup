import { bacsQueryOptions } from "@/features/bac/ui/options";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/eleve/_inscription/inscription/scolarite/")({
  loader: async ({ context: { queryClient } }) => {
    await queryClient.ensureQueryData(élèveQueryOptions);
    await queryClient.ensureQueryData(bacsQueryOptions);
  },
});
