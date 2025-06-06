import MaSélectionFavoris from "@/components/SélecteurFavoris/MaSélectionFavoris/MaSélectionFavoris";
import { i18n } from "@/configuration/i18n/i18n";
import useMaSélectionFormations from "@/features/formation/ui/SélectionFormationsFavorites/MaSélectionFormations/useMaSélectionFormations.ts";

const MaSélectionFormations = () => {
  const { favoris } = useMaSélectionFormations();

  return (
    <MaSélectionFavoris
      favoris={favoris}
      messageAucun={i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.MA_SÉLECTION.AUCUNE}
    />
  );
};

export default MaSélectionFormations;
