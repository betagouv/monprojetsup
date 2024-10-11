import { type StatusFormulaire } from "@/types/commons";

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
  status?: StatusFormulaire;
  auChangementValeurSélectionnée?: (valeur: string) => void;
  obligatoire?: boolean;
  registerHookForm?: {};
};
