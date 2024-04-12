import { rechercheQueryOptions } from "@/features/recherche/ui/options";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/recherche/")({
  loader: ({ context: { queryClient } }) => queryClient.ensureQueryData(rechercheQueryOptions),
});
