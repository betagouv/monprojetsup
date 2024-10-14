import { router } from "@/configuration/lib/tanstack-router";

export type StatusFormulaire = {
  type: "désactivé" | "erreur" | "succès";
  message?: string;
};

export type Paths = keyof (typeof router)["routesByPath"];
