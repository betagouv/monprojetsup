export type FiltresGroupésParCatégorieProps = {
  catégories: Array<{
    nom: string;
    emoji: string;
    filtres: Array<{
      id: string;
      nom: string;
    }>;
  }>;
  filtreIdsSélectionnésParDéfaut?: string[];
  auChangementFiltresSélectionnés: (filtreIdsSélectionnés: string[]) => void;
};
