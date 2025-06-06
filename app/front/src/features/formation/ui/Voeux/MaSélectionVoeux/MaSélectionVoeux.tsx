import useMaSélectionVoeux from "./useMaSélectionVoeux";
import MaSélectionFavoris from "@/components/SélecteurFavoris/MaSélectionFavoris/MaSélectionFavoris";
import { i18n } from "@/configuration/i18n/i18n";

const MaSélectionVoeux = () => {
  const { favoris } = useMaSélectionVoeux();

  return (
    <MaSélectionFavoris
      favoris={favoris}
      messageAucun={i18n.PAGE_FORMATION.CHOIX.VOEUX.MA_SÉLECTION.AUCUN}
    />
  );
};

export default MaSélectionVoeux;
