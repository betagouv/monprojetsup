import { type ReactNode } from "react";

export type CarteProps = {
  titre: string;
  id: string;
  estFavori: boolean;
  estMasqué: boolean;
  children: ReactNode;
  sélectionnée?: boolean;
};
