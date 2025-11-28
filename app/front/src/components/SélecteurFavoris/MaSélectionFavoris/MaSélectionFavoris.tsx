import { MaSélectionFavorisProps } from "./MaSélectionFavoris.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { i18n } from "@/configuration/i18n/i18n";
import { useMemo } from "react";

const MaSélectionFavoris = ({ favoris, messageAucun }: MaSélectionFavorisProps) => {
  const favorisSélectionnés = useMemo(() => favoris.filter((favori) => favori.estFavori), [favoris]);

  return (
    <div>
      <p className="mb-0 font-bold text-[--text-label-grey]">{i18n.COMMUN.FAVORIS.MA_SÉLÉCTION}</p>
      {favorisSélectionnés.length > 0 ? (
        <ListeDeFavoris favoris={favorisSélectionnés} />
      ) : (
        <p className="fr-text--sm mb-0">{messageAucun}</p>
      )}
    </div>
  );
};

export default MaSélectionFavoris;
