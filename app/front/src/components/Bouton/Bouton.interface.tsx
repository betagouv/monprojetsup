import { type BoutonSqueletteProps } from "@/components/BoutonSquelette/BoutonSquelette.interface";

export type BoutonProps = {
  label: BoutonSqueletteProps["label"];
  type: HTMLButtonElement["type"];
  auClic?: () => Promise<void> | void;
  taille?: BoutonSqueletteProps["taille"];
  variante?: BoutonSqueletteProps["variante"];
  désactivé?: boolean;
  icône?: BoutonSqueletteProps["icône"];
  formId?: string;
  ariaControls?: string;
  dataFrOpened?: string;
};
