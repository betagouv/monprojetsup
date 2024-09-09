import { type BoutonsActionsFicheFormationProps } from "./BoutonsActionsFicheFormation.interface";
import useBoutonsActionsFicheFormation from "./useBoutonsActionsFicheFormation";
import BoutonsActionsFormationMétier from "@/components/BoutonsActionsFormationMétier/BoutonsActionsFormationMétier";

const BoutonsActionsFicheFormation = ({ formation }: BoutonsActionsFicheFormationProps) => {
  const { estFavorite, ajouterEnFavori, supprimerDesFavoris, masquerUneFormation } = useBoutonsActionsFicheFormation({
    formation,
  });

  return (
    <BoutonsActionsFormationMétier
      ajouterEnFavoriCallback={ajouterEnFavori}
      estFavori={estFavorite}
      estUneFormation
      masquerCallback={masquerUneFormation}
      supprimerDesFavoris={supprimerDesFavoris}
      taille="grand"
    />
  );
};

export default BoutonsActionsFicheFormation;
