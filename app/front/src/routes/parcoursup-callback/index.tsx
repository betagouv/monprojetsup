import TemporaryConnexionParcourSup from "@/features/élève/ui/parcoursup/TemporaryConnexionParcourSup/TemporaryConnexionParcourSup";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

const parcourSupCallbackSearchSchema = z.object({
  code: z.string(),
});

export const Route = createFileRoute("/parcoursup-callback/")({
  validateSearch: (searchParamètres) => parcourSupCallbackSearchSchema.parse(searchParamètres),
  component: TemporaryConnexionParcourSup,
});
