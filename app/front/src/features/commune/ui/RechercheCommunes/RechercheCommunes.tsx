import useRechercheCommunes from "./useRechercheCommunes";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeCommunesSuggérées from "@/features/commune/ui/ListeDeCommunesSuggérées/ListeDeCommunesSuggérées";

const RechercheCommunes = () => {
  const { rechercher, communesSuggérées, rechercheEnCours } = useRechercheCommunes();

  return (
    <div>
      <Recherche
        description={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.DESCRIPTION}
        label={i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.LABEL}
        nombreDeCaractèresMaximumRecherche={constantes.COMMUNES.NB_CARACTÈRES_MAX_RECHERCHE}
        nombreDeCaractèresMinimumRecherche={constantes.COMMUNES.NB_CARACTÈRES_MIN_RECHERCHE}
        nombreDeRésultats={communesSuggérées?.length}
        rechercheCallback={rechercher}
      />
      {rechercheEnCours ? <AnimationChargement /> : <ListeDeCommunesSuggérées communes={communesSuggérées ?? []} />}
    </div>
  );
};

export default RechercheCommunes;
