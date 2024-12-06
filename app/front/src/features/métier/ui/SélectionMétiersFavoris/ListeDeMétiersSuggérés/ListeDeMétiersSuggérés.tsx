import { ListeDeMétiersSuggérésProps } from "./ListeDeMétiersSuggérés.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { constantes } from "@/configuration/constantes";
import useMétier from "@/features/métier/ui/SélectionMétiersFavoris/useMétier";

const ListeDeMétiersSuggérés = ({ métiers }: ListeDeMétiersSuggérésProps) => {
  const { métierVersFavori } = useMétier();
  const favoris = métiers.map(métierVersFavori);

  return (
    <ListeDeFavoris
      favoris={favoris}
      listeDeSuggestions
      nombreFavorisAffichésParDéfaut={constantes.MÉTIERS.NB_PAR_PAGE}
    />
  );
};

export default ListeDeMétiersSuggérés;
