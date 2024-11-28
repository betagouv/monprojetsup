import useVoeuxOngletToutesLesCommunes from "./useVoeuxOngletToutesLesCommunes";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Recherche from "@/components/Recherche/Recherche";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { Voeu } from "@/features/formation/domain/formation.interface";
import ListeDeVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/ListeDeVoeux/ListeDeVoeux";
import MaSélectionVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/MaSélectionVoeux/MaSélectionVoeux";

const VoeuxOngletToutesLesCommunes = () => {
  const { rechercher, voeuxSuggérés } = useVoeuxOngletToutesLesCommunes();

  return (
    <div className="grid gap-6">
      <div className="grid grid-flow-col items-center justify-start gap-4">
        <span
          aria-hidden="true"
          className=""
        >
          🏙️
        </span>
        <p className="fr-text--sm mb-0">
          {i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.RAPPEL}{" "}
          <LienInterne
            ariaLabel={i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PRÉFÉRENCES}
            hash="etude"
            href="/profil"
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PRÉFÉRENCES}
          </LienInterne>
        </p>
      </div>
      <Recherche<Voeu>
        description={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.DESCRIPTION}
        label={i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.LABEL}
        nombreDeCaractèresMinimumRecherche={constantes.VOEUX.NB_CARACTÈRES_MIN_RECHERCHE}
        rechercheCallback={rechercher}
      />
      <ListeDeVoeux voeux={voeuxSuggérés} />
      <hr className="pb-[1px]" />
      <div>
        <MaSélectionVoeux />
      </div>
    </div>
  );
};

export default VoeuxOngletToutesLesCommunes;
