import { type StatusFormulaire } from "@/types/commons";
import { type UseFormRegisterReturn } from "react-hook-form";

export type CurseurCrantéProps = {
  label: string;
  description?: string;
  valeurMax: number;
  valeurMin: number;
  valeurParDéfaut: number;
  pas?: number;
  status?: StatusFormulaire;
  registerHookForm?: UseFormRegisterReturn;
  auClicSurNeVeutPasRépondre: (neVeutPasRépondre: boolean) => void;
  neVeutPasRépondre?: boolean;
};
