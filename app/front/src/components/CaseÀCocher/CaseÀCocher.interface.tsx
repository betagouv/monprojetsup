import { type StatusFormulaire } from "@/types/commons";

export type CaseÀCocherProps = {
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
