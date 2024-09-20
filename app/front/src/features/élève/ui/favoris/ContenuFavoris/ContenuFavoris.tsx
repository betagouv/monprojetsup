import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";
import {
  catégorieAffichéeListeEtAperçuStore,
  élémentAffichéListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import FicheFormation from "@/features/formation/ui/FicheFormation/FicheFormation";
import FicheMétier from "@/features/métier/ui/FicheMétier/FicheMétier";
import { useQuery } from "@tanstack/react-query";

const ContenuFavoris = () => {
  const { data: élève } = useQuery(élèveQueryOptions);
  const élémentAffiché = élémentAffichéListeEtAperçuStore();
  const catégorieAffichée = catégorieAffichéeListeEtAperçuStore();

  if (élémentAffiché?.type === "métier")
    return <FicheMétier id={élémentAffiché?.id ?? élève?.métiersFavoris?.[0] ?? ""} />;

  if (élémentAffiché?.type === "formation")
    return <FicheFormation id={élémentAffiché?.id ?? élève?.formationsFavorites?.[0]?.id ?? ""} />;

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

export default ContenuFavoris;
