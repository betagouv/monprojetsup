export type BoutonRadioRicheProps = {
  légende: string;
  description?: string;
  options: Array<{
    valeur: string;
    label: string;
    description?: string;
    pictogramme: string;
  }>;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  registerHookForm?: {};
};
