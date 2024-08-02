import { centresIntêretsQueryOptions } from "@/features/centreIntêret/ui/centreIntêretQueries";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/eleve/_inscription/inscription/interets/")({
  loader: async ({ context: { queryClient } }) => {
    await queryClient.ensureQueryData(élèveQueryOptions);
    await queryClient.ensureQueryData(centresIntêretsQueryOptions);
  },
});
