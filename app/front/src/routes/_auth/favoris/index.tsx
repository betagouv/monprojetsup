import FavorisPage from "@/features/élève/ui/favoris/FavorisPage/FavorisPage";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/favoris/")({
  component: FavorisPage,
  loader: ({ context: { queryClient } }) => {
    queryClient.removeQueries({ queryKey: ["formations"] });
    queryClient.removeQueries({ queryKey: ["métiers"] });
  },
});
