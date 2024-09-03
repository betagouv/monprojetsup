import { type router } from "@/configuration/lib/tanstack-router";
import { type ReactNode } from "react";

export type LienInterneProps = {
  children: ReactNode;
  href: keyof (typeof router)["routesByPath"];
  ariaLabel: string;
  hash?: string;
  taille?: "petit" | "grand";
  variante?: "neutre" | "simple";
  icône?: {
    position: "droite" | "gauche";
    classe: string;
  };
  estUnTag?: boolean;
};
