import { type StatusFormulaire } from "@/types/commons";

export type BoutonRadioProps = {
  légende: string;
  description?: string;
  options: Array<{
    valeur: string;
    label: string;
    description?: string;
  }>;
  status?: StatusFormulaire;
  obligatoire?: boolean;
  registerHookForm?: {};
};
