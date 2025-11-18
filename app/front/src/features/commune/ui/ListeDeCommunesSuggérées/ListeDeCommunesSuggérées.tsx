import { ListeDeCommunesSuggéréesProps } from "./ListeDeCommunesSuggérées.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { constantes } from "@/configuration/constantes";
import useCommune from "@/features/commune/ui/useCommune";

const ListeDeCommunesSuggérées = ({ communes }: ListeDeCommunesSuggéréesProps) => {
  const { communeVersFavori } = useCommune();
  const favoris = communes.map(communeVersFavori);

  return (
    <ListeDeFavoris
      favoris={favoris}
      listeDeSuggestions
      nombreFavorisAffichésParDéfaut={constantes.COMMUNES.NB_PAR_PAGE}
    />
  );
};

export default ListeDeCommunesSuggérées;
