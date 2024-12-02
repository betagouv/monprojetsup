import { ListeDeVoeuxProps } from "./ListeDeVoeux.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { constantes } from "@/configuration/constantes";
import useVoeu from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/useVoeu";

const ListeDeVoeux = ({ voeux }: ListeDeVoeuxProps) => {
  const { voeuVersFavori } = useVoeu();
  const favoris = voeux.map(voeuVersFavori);

  return (
    <ListeDeFavoris
      favoris={favoris}
      nombreFavorisAffichésParDéfaut={constantes.VOEUX.NB_VOEUX_PAR_PAGE}
    />
  );
};

export default ListeDeVoeux;
