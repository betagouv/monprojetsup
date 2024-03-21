import Navigation from "@/components/_layout/Navigation/Navigation";
import { i18n } from "@/configuration/i18n/i18n";
import { Link } from "@tanstack/react-router";

const Header = () => {
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
                    République
                    <br />
                    française
                  </p>
                </div>
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
              </div>
              <div className="fr-header__service">
                <Link
                  title={`Accueil - ${i18n.APP.NAME}`}
                  to="/"
                >
                  <p className="fr-header__service-title">{i18n.APP.NAME}</p>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Navigation />
    </header>
  );
};

export default Header;
