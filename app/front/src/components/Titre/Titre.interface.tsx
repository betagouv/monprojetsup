import { type ReactNode } from "react";

export type TitreProps = {
  children: ReactNode;
  niveauDeTitre: "h1" | "h2" | "h3" | "h4" | "h5" | "h6";
  styleDeTitre?:
    | "h1"
    | "h2"
    | "h3"
    | "h4"
    | "h5"
    | "h6"
    | "display--xs"
    | "display--md"
    | "display--lg"
    | "display--xl"
    | "text--lead"
    | "text--lg"
    | "text"
    | "text--md"
    | "text--sm"
    | "text--xs";
};
