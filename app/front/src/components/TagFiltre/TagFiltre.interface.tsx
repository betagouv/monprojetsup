import { type ReactNode } from "react";

export type TagFiltreProps = {
  children: ReactNode;
  ariaLabel: string;
  auClic: (estAppuyé: boolean) => void;
  taille?: "petit";
  emoji?: string;
  appuyéParDéfaut?: boolean;
};
