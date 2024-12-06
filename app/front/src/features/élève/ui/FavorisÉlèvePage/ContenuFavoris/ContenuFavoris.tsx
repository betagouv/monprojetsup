import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import AucunFavoris from "@/features/élève/ui/FavorisÉlèvePage/AucunFavoris/AucunFavoris";
import FicheFormation from "@/features/formation/ui/FormationPage/FicheFormation/FicheFormation";
import FicheMétier from "@/features/métier/ui/FicheMétier/FicheMétier";

const ContenuFavoris = () => {
  const élémentAffiché = élémentAffichéListeEtAperçuStore();

  if (élémentAffiché.type === "métier" && élémentAffiché.id) return <FicheMétier id={élémentAffiché.id} />;
  if (élémentAffiché.type === "formation" && élémentAffiché.id) return <FicheFormation id={élémentAffiché.id} />;

  return <AucunFavoris catégorie={élémentAffiché.type} />;
};

export default ContenuFavoris;
