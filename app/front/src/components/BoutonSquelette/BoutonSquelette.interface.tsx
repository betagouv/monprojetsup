export type BoutonSqueletteProps = {
  label: string;
  taille?: "petit" | "grand";
  variante?: "secondaire" | "tertiaire" | "quaternaire" | "quinaire";
  icône?: {
    position?: "droite" | "gauche";
    classe: string;
  };
};
