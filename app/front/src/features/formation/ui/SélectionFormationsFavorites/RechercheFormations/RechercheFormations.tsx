import useRechercheFormations from "./useRechercheFormations";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeFormationsSuggérées from "@/features/formation/ui/SélectionFormationsFavorites/ListeDeFormationsSuggérées/ListeDeFormationsSuggérées";

const RechercheFormations = () => {
  const { rechercher, formationsSuggérées, rechercheEnCours } = useRechercheFormations();

  return (
    <div>
      <Recherche
        description={i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.DESCRIPTION}
        label={i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.LABEL}
        nombreDeCaractèresMaximumRecherche={constantes.FORMATIONS.NB_CARACTÈRES_MAX_RECHERCHE}
        nombreDeCaractèresMinimumRecherche={constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE}
        nombreDeRésultats={formationsSuggérées?.length}
        rechercheCallback={rechercher}
      />
      {rechercheEnCours ? (
        <AnimationChargement />
      ) : (
        <ListeDeFormationsSuggérées formations={formationsSuggérées ?? []} />
      )}
    </div>
  );
};

export default RechercheFormations;
