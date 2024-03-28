import MaScolaritéForm from "@/features/élève/ui/MaScolaritéForm/MaScolaritéForm";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_inscription/inscription/scolarite/")({
  component: MaScolaritéForm,
});
