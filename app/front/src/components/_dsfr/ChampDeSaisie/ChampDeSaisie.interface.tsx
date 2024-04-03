export type ChampDeSaisieProps = {
  label: string;
  name: string;
  description?: string;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  icône?: string;
  registerHookForm?: {};
};
