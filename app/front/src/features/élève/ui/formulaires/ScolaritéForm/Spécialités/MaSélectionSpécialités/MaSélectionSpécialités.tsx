import useMaSélectionSpécialités from "./useMaSélectionSpécialités";
import MaSélectionFavoris from "@/components/SélecteurFavoris/MaSélectionFavoris/MaSélectionFavoris";
import { i18n } from "@/configuration/i18n/i18n";

const MaSélectionSpécialités = () => {
  const { spécialitésSélectionnées } = useMaSélectionSpécialités();

  return (
    <MaSélectionFavoris
      favoris={spécialitésSélectionnées}
      messageAucun={i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.MA_SÉLECTION.AUCUNE}
    />
  );
};

export default MaSélectionSpécialités;
