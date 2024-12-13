import { ReactNode } from "react";

export type BoutonSqueletteProps = {
  children: ReactNode;
  taille?: "petit" | "grand";
  variante?: "secondaire" | "tertiaire" | "quaternaire" | "quinaire";
  icône?: {
    position?: "droite" | "gauche";
    classe: string;
  };
};
