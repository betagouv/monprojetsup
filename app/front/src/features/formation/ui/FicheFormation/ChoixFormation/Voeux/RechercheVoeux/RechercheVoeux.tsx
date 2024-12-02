import useRechercheVoeux from "./useRechercheVoeux";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { Voeu } from "@/features/formation/domain/formation.interface";
import ListeDeVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/ListeDeVoeux/ListeDeVoeux";

const RechercheVoeux = () => {
  const { rechercher, voeuxSuggérés } = useRechercheVoeux();

  return (
    <>
      <Recherche<Voeu>
        description={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.DESCRIPTION}
        label={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.LABEL}
        nombreDeCaractèresMinimumRecherche={constantes.VOEUX.NB_CARACTÈRES_MIN_RECHERCHE}
        rechercheCallback={rechercher}
      />
      <ListeDeVoeux voeux={voeuxSuggérés} />
    </>
  );
};

export default RechercheVoeux;
