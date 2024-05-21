import ProfilÉlèvePage from "@/features/élève/ui/profil/ProfilÉlèvePage/ProfilÉlèvePage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/eleve/profil/")({
  component: ProfilÉlèvePage,
});
