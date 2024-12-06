import ProfilÉlèvePage from "@/features/élève/ui/ProfilÉlèvePage/ProfilÉlèvePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/profil/")({
  component: ProfilÉlèvePage,
});
