import { élèveQueryOptions } from "@/features/élève/ui/options";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_inscription/inscription/domaines/")({
  loader: ({ context: { queryClient } }) => queryClient.ensureQueryData(élèveQueryOptions),
});
