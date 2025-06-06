import { AucunFavorisProps } from "./AucunFavoris.interface";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";

const AucunFavoris = ({ catégorie }: AucunFavorisProps) => {
  return (
    <div className="my-40 grid place-items-center gap-2 text-center">
      <div
        aria-hidden="true"
        className="fr-display--lg fr-mb-0"
      >
        {i18n.PAGE_FAVORIS.AUCUN_FAVORI.EMOJI}
      </div>
      <p className="fr-display--xs mb-0">{i18n.PAGE_FAVORIS.AUCUN_FAVORI.OUPS}</p>
      <div className="*:mb-0">
        <Titre
          niveauDeTitre="h1"
          styleDeTitre="h3"
        >
          {catégorie === "formation"
            ? i18n.PAGE_FAVORIS.AUCUN_FAVORI.TEXTE_FORMATIONS
            : i18n.PAGE_FAVORIS.AUCUN_FAVORI.TEXTE_MÉTIERS}
        </Titre>
      </div>
      <div className="mt-5">
        <LienInterne
          ariaLabel={i18n.PAGE_FAVORIS.AUCUN_FAVORI.BOUTON}
          href="/formations"
          variante="neutre"
        >
          <BoutonSquelette
            icône={{ position: "droite", classe: "fr-icon-arrow-right-line" }}
            taille="grand"
          >
            {i18n.PAGE_FAVORIS.AUCUN_FAVORI.BOUTON}
          </BoutonSquelette>
        </LienInterne>
      </div>
    </div>
  );
};

export default AucunFavoris;
