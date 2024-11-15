import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import FicheMétier from "@/features/métier/ui/FicheMétier/FicheMétier";
import AucunFavoris from "@/features/élève/ui/favoris/AucunFavoris/AucunFavoris";

const ContenuFavoris = () => {
  const élémentAffiché = élémentAffichéListeEtAperçuStore();

  if (élémentAffiché.type === "métier" && élémentAffiché.id) return <FicheMétier id={élémentAffiché.id} />;
  if (élémentAffiché.type === "formation" && élémentAffiché.id) return <FicheFormation id={élémentAffiché.id} />;

  return <AucunFavoris catégorie={élémentAffiché.type} />;
};

export default ContenuFavoris;
