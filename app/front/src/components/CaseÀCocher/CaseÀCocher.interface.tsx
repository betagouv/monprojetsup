import { type StatusFormulaire } from "@/types/commons";
import { UseFormRegisterReturn } from "react-hook-form";

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
  registerHookForm?: UseFormRegisterReturn;
};
