import { type ReactNode } from "react";

export type LienExterneProps = {
  children: ReactNode;
  href: string;
  ariaLabel: string;
  taille?: "petit" | "grand";
  variante?: "neutre" | "simple";
  icône?: {
    position: "droite" | "gauche";
    classe: string;
  };
  estUnTéléchargement?: boolean;
};
