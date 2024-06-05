export type FiltresGroupésParCatégorieProps = {
  catégories: Array<{
    nom: string;
    emoji: string;
    filtres: Array<{
      id: string;
      nom: string;
    }>;
  }>;
  niveauDeTitre: "h2" | "h3";
  filtreIdsSélectionnésParDéfaut?: string[];
  auChangementFiltresSélectionnés: (filtreIdsSélectionnés: string[]) => void;
};
