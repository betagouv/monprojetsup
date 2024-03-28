export type BoutonRadioRicheProps = {
  légende: string;
  description?: string;
  options: Array<{
    id: string;
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
