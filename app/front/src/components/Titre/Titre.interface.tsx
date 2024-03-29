import { type ReactNode } from "react";

export type TitreProps = {
  children: ReactNode;
  niveauDeTitre: "h1" | "h2" | "h3" | "h4" | "h5" | "h6";
  styleDeTitre?: "h1" | "h2" | "h3" | "h4" | "h5" | "h6";
};
