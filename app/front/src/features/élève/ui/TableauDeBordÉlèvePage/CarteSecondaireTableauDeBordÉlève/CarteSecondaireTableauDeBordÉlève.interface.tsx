import { ReactNode } from "@tanstack/react-router";

export type CarteSecondaireTableauDeBordÉlèveProps = {
  titre: string;
  sousTitre: string;
  illustration: string;
  altIllustration?: string;
  children: ReactNode;
};
