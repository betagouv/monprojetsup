import { catégorieAffichéeListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import BoutonSquelette from "@/components/BoutonSquelette/BoutonSquelette";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";

const AucunFavoris = () => {
  const catégorieAffichée = catégorieAffichéeListeEtAperçuStore();

  return (
    <div className="my-40 grid place-items-center gap-2 text-center">
      <div
        aria-hidden="true"
        className="fr-display--lg fr-mb-0"
      >
        {i18n.PAGE_FAVORIS.AUCUN_FAVORI.EMOJI}
      </div>
      <p className="fr-display--xs mb-0">{i18n.PAGE_FAVORIS.AUCUN_FAVORI.OUPS}</p>
      <p className="fr-h3 mb-0">
        {catégorieAffichée === "première"
          ? i18n.PAGE_FAVORIS.AUCUN_FAVORI.TEXTE_FORMATIONS
          : i18n.PAGE_FAVORIS.AUCUN_FAVORI.TEXTE_MÉTIERS}
      </p>
      <div className="mt-5">
        <LienInterne
          ariaLabel={i18n.PAGE_FAVORIS.AUCUN_FAVORI.BOUTON}
          href="/formations"
          variante="neutre"
        >
          <BoutonSquelette
            icône={{ position: "droite", classe: "fr-icon-arrow-right-line" }}
            label={i18n.PAGE_FAVORIS.AUCUN_FAVORI.BOUTON}
            taille="grand"
          />
        </LienInterne>
      </div>
    </div>
  );
};

export default AucunFavoris;
