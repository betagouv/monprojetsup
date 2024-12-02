import { MaSélectionFavorisProps } from "./MaSélectionFavoris.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { useMemo } from "react";

const MaSélectionFavoris = ({ favoris, niveauDeTitre, messageAucun }: MaSélectionFavorisProps) => {
  const favorisSélectionnés = useMemo(() => favoris.filter((favori) => favori.estFavori), [favoris]);

  return (
    <>
      <div className="*:mb-2">
        <Titre
          niveauDeTitre={niveauDeTitre}
          styleDeTitre="text--md"
        >
          {i18n.COMMUN.FAVORIS.MA_SÉLÉCTION}
        </Titre>
      </div>
      {favorisSélectionnés.length > 0 ? (
        <ListeDeFavoris favoris={favorisSélectionnés} />
      ) : (
        <p className="fr-text--sm mb-0">{messageAucun}</p>
      )}
    </>
  );
};

export default MaSélectionFavoris;
