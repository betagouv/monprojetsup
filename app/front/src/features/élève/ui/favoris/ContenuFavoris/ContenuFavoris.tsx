import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import FicheMétier from "@/features/métier/ui/FicheMétier/FicheMétier";
import AucunFavoris from "@/features/élève/ui/favoris/AucunFavoris/AucunFavoris";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const ContenuFavoris = () => {
  const { data: élève } = useQuery(élèveQueryOptions);
  const élémentAffiché = élémentAffichéListeEtAperçuStore();

  if (élémentAffiché?.type === "métier")
    return <FicheMétier id={élémentAffiché?.id ?? élève?.métiersFavoris?.[0] ?? ""} />;

  if (élémentAffiché?.type === "formation")
    return <FicheFormation id={élémentAffiché?.id ?? élève?.formationsFavorites?.[0]?.id ?? ""} />;

  return <AucunFavoris />;
};

export default ContenuFavoris;
