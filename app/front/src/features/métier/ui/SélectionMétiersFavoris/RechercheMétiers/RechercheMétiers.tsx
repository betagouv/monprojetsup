import useRechercheMétiers from "./useRechercheMétiers";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeMétiersSuggérés from "@/features/métier/ui/SélectionMétiersFavoris/ListeDeMétiersSuggérés/ListeDeMétiersSuggérés";

const RechercheMétiers = () => {
  const { rechercher, métiersSuggérés, rechercheEnCours } = useRechercheMétiers();

  return (
    <div>
      <Recherche
        description={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.DESCRIPTION}
        label={i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL}
        nombreDeCaractèresMaximumRecherche={constantes.MÉTIERS.NB_CARACTÈRES_MAX_RECHERCHE}
        nombreDeCaractèresMinimumRecherche={constantes.MÉTIERS.NB_CARACTÈRES_MIN_RECHERCHE}
        nombreDeRésultats={métiersSuggérés?.length}
        rechercheCallback={rechercher}
      />
      {rechercheEnCours ? <AnimationChargement /> : <ListeDeMétiersSuggérés métiers={métiersSuggérés ?? []} />}
    </div>
  );
};

export default RechercheMétiers;
