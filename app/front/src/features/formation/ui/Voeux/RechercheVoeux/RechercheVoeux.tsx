import useRechercheVoeux from "./useRechercheVoeux";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeVoeuxSuggérés from "@/features/formation/ui/Voeux/ListeDeVoeuxSuggérés/ListeDeVoeuxSuggérés";

const RechercheVoeux = () => {
  const { rechercher, voeuxSuggérés } = useRechercheVoeux();

  return (
    <>
      <Recherche
        description={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.DESCRIPTION}
        label={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.LABEL}
        nombreDeCaractèresMaximumRecherche={constantes.VOEUX.NB_CARACTÈRES_MAX_RECHERCHE}
        nombreDeCaractèresMinimumRecherche={constantes.VOEUX.NB_CARACTÈRES_MIN_RECHERCHE}
        nombreDeRésultats={voeuxSuggérés?.length}
        rechercheCallback={rechercher}
      />
      <ListeDeVoeuxSuggérés voeux={voeuxSuggérés ?? []} />
    </>
  );
};

export default RechercheVoeux;
