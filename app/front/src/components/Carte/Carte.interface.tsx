import { type ReactNode } from "react";

export type CarteProps = {
  titre: string;
  estFavori: boolean;
  estMasqué: boolean;
  children: ReactNode;
  auClic: () => void;
  sélectionnée?: boolean;
};
