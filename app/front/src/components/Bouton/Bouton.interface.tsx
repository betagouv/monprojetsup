import { type BoutonSqueletteProps } from "@/components/BoutonSquelette/BoutonSquelette.interface";
import { AriaRole } from "react";

export type BoutonProps = {
  children: BoutonSqueletteProps["children"];
  type: HTMLButtonElement["type"];
  auClic?: () => Promise<unknown> | void;
  taille?: BoutonSqueletteProps["taille"];
  variante?: BoutonSqueletteProps["variante"];
  désactivé?: boolean;
  icône?: BoutonSqueletteProps["icône"];
  formId?: string;
  ariaControls?: string;
  dataFrOpened?: string;
  rôle?: AriaRole;
};
