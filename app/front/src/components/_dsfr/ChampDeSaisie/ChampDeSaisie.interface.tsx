export type ChampDeSaisieProps = {
  label: string;
  description?: string;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  icône?: string;
  auChangement?: React.ChangeEventHandler<HTMLInputElement>;
  obligatoire?: boolean;
  registerHookForm?: {};
  estChampDeRecherche?: boolean;
};
