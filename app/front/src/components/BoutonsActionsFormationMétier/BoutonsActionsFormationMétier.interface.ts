import { type BoutonProps } from "@/components/Bouton/Bouton.interface";

export type BoutonsActionsFormationMétierProps = {
  estFavori: boolean;
  estUneFormation: boolean;
  ajouterEnFavoriCallback?: () => void;
  supprimerDesFavoris?: () => void;
  masquerCallback?: () => void;
  taille?: BoutonProps["taille"];
};
