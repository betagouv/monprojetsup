import { type StatusFormulaire } from "@/types/commons";

export type ListeDéroulanteProps = {
  label: string;
  description?: string;
  options: Array<{ valeur: string; label: string }>;
  status?: StatusFormulaire;
  obligatoire?: boolean;
  valeurOptionSélectionnéeParDéfaut?: string;
  registerHookForm?: {};
};
