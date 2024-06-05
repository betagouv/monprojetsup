export type useLienProps = {
  href: string;
  ariaLabel: string;
  taille?: "petit" | "grand";
  variante?: "neutre" | "simple";
  icône?: {
    position: "droite" | "gauche";
    classe: string;
  };
  estUnTéléchargement?: boolean;
  estUnTag?: boolean;
  estUnBouton?: boolean;
};
