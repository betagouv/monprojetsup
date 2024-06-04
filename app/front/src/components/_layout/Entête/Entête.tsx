import logoMPS from "@/assets/logo/logo-mps.svg";
import Navigation from "@/components/_layout/Navigation/Navigation";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import { useRouterState } from "@tanstack/react-router";

const Entête = () => {
  const router = useRouterState();

  return (
    <header
      className="fr-header"
      role="banner"
    >
      <div className="fr-header__body">
        <div className="fr-container">
          <div className="fr-header__body-row">
            <div className="fr-header__brand fr-enlarge-link">
              <div className="fr-header__brand-top">
                <div className="fr-header__logo">
                  <p className="fr-logo">
                    Ministère
                    <br />
                    de l’enseignement
                    <br />
                    supérieur
                    <br />
                    et de la recherche
                  </p>
                </div>
                <div className="fr-header__operator">
                  <img
                    alt="MPS"
                    className="fr-responsive-img"
                    src={logoMPS}
                  />
                </div>
                {!router.location.pathname.includes("/eleve/inscription/") && (
                  <div className="fr-header__navbar">
                    <button
                      aria-controls="navigation-modal"
                      aria-haspopup="menu"
                      className="fr-btn--menu fr-btn"
                      data-fr-opened="false"
                      id="navigation-burger-button"
                      title={i18n.NAVIGATION.MAIN_NAVIGATION}
                      type="button"
                    >
                      {i18n.NAVIGATION.MAIN_NAVIGATION}
                    </button>
                  </div>
                )}
              </div>
              <div className="fr-header__service">
                <LienInterne
                  ariaLabel={`Accueil - ${i18n.APP.NOM}`}
                  href="/"
                >
                  <p className="fr-header__service-title">{i18n.APP.NOM}</p>
                </LienInterne>
                <p className="fr-header__service-tagline">{i18n.ENTÊTE.DESCRIPTION_SERVICE}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      {!router.location.pathname.includes("/eleve/inscription/") && <Navigation />}
    </header>
  );
};

export default Entête;
