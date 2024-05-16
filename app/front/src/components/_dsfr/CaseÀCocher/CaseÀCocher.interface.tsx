export type CaseÀCocherProps = {
  légende: string;
  description?: string;
  options: Array<{
    valeur: string;
    label: string;
    description?: string;
  }>;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  obligatoire?: boolean;
  registerHookForm?: {};
};
