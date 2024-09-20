export type BoutonRadioRicheProps = {
  légende: string;
  description?: string;
  options: Array<{
    valeur: string;
    label: string;
    description?: string;
    pictogramme: string;
    cochéParDéfaut?: boolean;
  }>;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  auChangementValeurSélectionnée?: (valeur: string) => void;
  obligatoire?: boolean;
  registerHookForm?: {};
};
