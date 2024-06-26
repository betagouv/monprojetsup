import { type BoutonSqueletteProps } from "@/components/_dsfr/BoutonSquelette/BoutonSquelette.interface";

export type BoutonProps = {
  label: BoutonSqueletteProps["label"];
  type: HTMLButtonElement["type"];
  auClic?: () => void;
  taille?: BoutonSqueletteProps["taille"];
  variante?: BoutonSqueletteProps["variante"];
  désactivé?: boolean;
  icône?: BoutonSqueletteProps["icône"];
  formId?: string;
  ariaControls?: string;
  dataFrOpened?: string;
};
