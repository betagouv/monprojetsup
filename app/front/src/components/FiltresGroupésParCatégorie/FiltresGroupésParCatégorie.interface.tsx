export type FiltresGroupésParCatégorieProps = {
  catégories: Array<{
    nom: string;
    emoji: string;
    afficherDétail: boolean;
    filtres: Array<{
      id: string;
      nom: string;
      description: string | null;
      emoji: string;
    }>;
  }>;
  niveauDeTitre: "h2" | "h3";
  filtreIdsSélectionnésParDéfaut?: string[];
  auChangementFiltresSélectionnés: (filtreIdsSélectionnés: string[]) => void;
};
