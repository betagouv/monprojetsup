export type BoutonProps = {
  label: string;
  type: HTMLButtonElement["type"];
  auClic?: () => void;
  taille?: "petit" | "grand";
  variante?: "secondaire" | "tertiaire" | "quaternaire";
  desactivé?: boolean;
  icône?: {
    position: "droite" | "gauche";
    classe: string;
  };
};
