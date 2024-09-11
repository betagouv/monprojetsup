import { type BoutonsActionsFicheFormationProps } from "./BoutonsActionsFicheFormation.interface";
import useBoutonsActionsFicheFormation from "./useBoutonsActionsFicheFormation";
import BoutonsActionsFormationMétier from "@/components/BoutonsActionsFormationMétier/BoutonsActionsFormationMétier";

const BoutonsActionsFicheFormation = ({ formation }: BoutonsActionsFicheFormationProps) => {
  const {
    estFavorite,
    estMasquée,
    ajouterEnFavori,
    supprimerDesFavoris,
    masquerUneFormation,
    afficherÀNouveauUneFormation,
  } = useBoutonsActionsFicheFormation({
    formation,
  });

  return (
    <BoutonsActionsFormationMétier
      afficherÀNouveauCallback={afficherÀNouveauUneFormation}
      ajouterEnFavoriCallback={ajouterEnFavori}
      estFavori={estFavorite}
      estMasqué={estMasquée}
      estUneFormation
      masquerCallback={masquerUneFormation}
      supprimerDesFavoris={supprimerDesFavoris}
      taille="grand"
    />
  );
};

export default BoutonsActionsFicheFormation;
