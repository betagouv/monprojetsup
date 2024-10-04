import ProfilÉlève from "@/features/élève/ui/profil/ProfilÉlève/ProfilÉlève";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/profil/")({
  component: ProfilÉlève,
});
