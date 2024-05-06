import { élèveQueryOptions } from "@/features/élève/ui/options";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/eleve/tableau-de-bord/")({
  loader: async ({ context: { queryClient } }) => {
    await queryClient.ensureQueryData(élèveQueryOptions);
  },
});
