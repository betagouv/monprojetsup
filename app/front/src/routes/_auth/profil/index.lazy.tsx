import ProfilPage from "@/components/_pages/ProfilPage/ProfilPage";
import { createLazyFileRoute } from "@tanstack/react-router";

export const Route = createLazyFileRoute("/_auth/profil/")({
  component: ProfilPage,
});
