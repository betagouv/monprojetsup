import useMaSélectionCommunes from "./useMaSélectionCommunes";
import MaSélectionFavoris from "@/components/SélecteurFavoris/MaSélectionFavoris/MaSélectionFavoris";
import { i18n } from "@/configuration/i18n/i18n";

const MaSélectionCommunes = () => {
  const { favoris } = useMaSélectionCommunes();

  return (
    <MaSélectionFavoris
      favoris={favoris}
      messageAucun={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.MA_SÉLECTION.AUCUNE}
    />
  );
};

export default MaSélectionCommunes;
