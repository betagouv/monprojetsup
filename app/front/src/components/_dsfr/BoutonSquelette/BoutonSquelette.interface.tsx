export type BoutonSqueletteProps = {
  label: string;
  taille?: "petit" | "grand";
  variante?: "secondaire" | "tertiaire" | "quaternaire";
  icône?: {
    position: "droite" | "gauche";
    classe: string;
  };
};
