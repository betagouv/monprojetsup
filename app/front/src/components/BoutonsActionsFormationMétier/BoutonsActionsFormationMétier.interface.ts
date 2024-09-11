import { type BoutonProps } from "@/components/Bouton/Bouton.interface";

export type BoutonsActionsFormationMétierProps = {
  estFavori: boolean;
  estMasqué: boolean;
  estUneFormation: boolean;
  ajouterEnFavoriCallback?: () => void;
  supprimerDesFavoris?: () => void;
  masquerCallback?: () => void;
  afficherÀNouveauCallback?: () => void;
  taille?: BoutonProps["taille"];
};
