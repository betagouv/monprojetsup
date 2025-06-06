import useMaSélectionMétiers from "./useMaSélectionMétiers";
import MaSélectionFavoris from "@/components/SélecteurFavoris/MaSélectionFavoris/MaSélectionFavoris";
import { i18n } from "@/configuration/i18n/i18n";

const MaSélectionMétiers = () => {
  const { favoris } = useMaSélectionMétiers();

  return (
    <MaSélectionFavoris
      favoris={favoris}
      messageAucun={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.MA_SÉLECTION.AUCUN}
    />
  );
};

export default MaSélectionMétiers;
