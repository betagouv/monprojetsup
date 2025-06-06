import { ListeDeFormationsSuggéréesProps } from "./ListeDeFormationsSuggérées.interface";
import ListeDeFavoris from "@/components/SélecteurFavoris/ListeDeFavoris/ListeDeFavoris";
import { constantes } from "@/configuration/constantes";
import useFormation from "@/features/formation/ui/useFormation";

const ListeDeFormationsSuggérées = ({ formations }: ListeDeFormationsSuggéréesProps) => {
  const { formationVersFavori } = useFormation();
  const favoris = formations.map(formationVersFavori);

  return (
    <ListeDeFavoris
      favoris={favoris}
      listeDeSuggestions
      nombreFavorisAffichésParDéfaut={constantes.FORMATIONS.NB_PAR_PAGE}
    />
  );
};

export default ListeDeFormationsSuggérées;
