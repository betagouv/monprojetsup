import { ListeDeSpécialitésSuggéréesProps } from "./ListeDeSpécialitésSuggérées.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { constantes } from "@/configuration/constantes";
import useSpécialité from "@/features/élève/ui/formulaires/ScolaritéForm/Spécialités/useSpécialité";

const ListeDeSpécialitésSuggérées = ({ spécialités }: ListeDeSpécialitésSuggéréesProps) => {
  const { spécialitéVersFavori } = useSpécialité();
  const favoris = spécialités.map(spécialitéVersFavori);

  return (
    <ListeDeFavoris
      favoris={favoris}
      listeDeSuggestions
      nombreFavorisAffichésParDéfaut={constantes.SPÉCIALITÉS.NB_PAR_PAGE}
    />
  );
};

export default ListeDeSpécialitésSuggérées;
