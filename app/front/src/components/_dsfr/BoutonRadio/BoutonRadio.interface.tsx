export type BoutonRadioProps = {
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
  registerHookForm?: {};
};
