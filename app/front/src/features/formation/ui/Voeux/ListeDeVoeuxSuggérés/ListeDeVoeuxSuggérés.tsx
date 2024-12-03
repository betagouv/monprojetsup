import { ListeDeVoeuxSuggérésProps } from "./ListeDeVoeuxSuggérés.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { constantes } from "@/configuration/constantes";
import useVoeu from "@/features/formation/ui/Voeux/useVoeu";

const ListeDeVoeuxSuggérés = ({ voeux }: ListeDeVoeuxSuggérésProps) => {
  const { voeuVersFavori } = useVoeu();
  const favoris = voeux.map(voeuVersFavori);

  return (
    <ListeDeFavoris
      favoris={favoris}
      listeDeSuggestions
      nombreFavorisAffichésParDéfaut={constantes.VOEUX.NB_VOEUX_PAR_PAGE}
    />
  );
};

export default ListeDeVoeuxSuggérés;
