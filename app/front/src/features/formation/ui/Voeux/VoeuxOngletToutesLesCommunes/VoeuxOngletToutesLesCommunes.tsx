import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import MaSélectionVoeux from "@/features/formation/ui/Voeux/MaSélectionVoeux/MaSélectionVoeux";
import RechercheVoeux from "@/features/formation/ui/Voeux/RechercheVoeux/RechercheVoeux";

const VoeuxOngletToutesLesCommunes = () => {
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
      <RechercheVoeux />
      <hr className="pb-[1px]" />
      <div>
        <MaSélectionVoeux />
      </div>
    </div>
  );
};

export default VoeuxOngletToutesLesCommunes;
