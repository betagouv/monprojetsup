import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import { Paths } from "@/types/commons";
import { useRouterState } from "@tanstack/react-router";

const LienÉvitement = () => {
  const routerState = useRouterState();
  const routeActuelle = routerState.location.pathname as Paths;

  return (
    <div className="fr-skiplinks">
      <nav
        aria-label="Accès rapide"
        className="fr-container"
        role="navigation"
      >
        <ul className="fr-skiplinks__list">
          <li>
            <LienInterne
              ariaLabel={i18n.ACCESSIBILITÉ.CONTENU}
              hash="contenu"
              href={routeActuelle}
            >
              {i18n.ACCESSIBILITÉ.CONTENU}
            </LienInterne>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default LienÉvitement;
