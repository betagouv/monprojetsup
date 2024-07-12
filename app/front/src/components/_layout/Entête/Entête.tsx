import logoMPS from "@/assets/logo/logo-mps.svg";
import Navigation from "@/components/_layout/Navigation/Navigation";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import { useRouterState } from "@tanstack/react-router";

const Entête = () => {
  const router = useRouterState();
  const utilisateur = useUtilisateur();

  const PARCOURS_INSCRIPTION_PATH = "/eleve/inscription/";
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
                {!router.location.pathname.includes(PARCOURS_INSCRIPTION_PATH) && (
                  <div className="fr-header__navbar">
                    <button
                      aria-controls="navigation-modal"
                      aria-haspopup="menu"
                      className="fr-btn--menu fr-btn"
                      data-fr-opened="false"
                      id="navigation-burger-button"
                      title={i18n.NAVIGATION.TABLEAU_DE_BORD}
                      type="button"
                    >
                      {i18n.NAVIGATION.TABLEAU_DE_BORD}
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
            {!router.location.pathname.includes(PARCOURS_INSCRIPTION_PATH) && (
              <div className="fr-header__tools">
                <div className="fr-header__tools-links">
                  <ul className="fr-btns-group">
                    <li>
                      <LienExterne
                        ariaLabel={i18n.ENTÊTE.PLATEFORME_AVENIRS}
                        href="https://avenirs.onisep.fr/"
                        icône={{ classe: "fr-icon-arrow-go-back-fill", position: "droite" }}
                      >
                        <span className="fr-btn fr-icon-arrow-go-back-fill">{i18n.ENTÊTE.PLATEFORME_AVENIRS}</span>
                      </LienExterne>
                    </li>
                    {utilisateur.type ? (
                      <li>
                        <LienInterne
                          ariaLabel={`${utilisateur.prénom} ${utilisateur.nom}` ?? i18n.ENTÊTE.MON_ESPACE}
                          href="/profil"
                        >
                          <span className="fr-btn fr-icon-user-fill">
                            {`${utilisateur.prénom} ${utilisateur.nom}` ?? i18n.ENTÊTE.MON_ESPACE}
                          </span>
                        </LienInterne>
                      </li>
                    ) : (
                      <li>
                        <LienInterne
                          ariaLabel={i18n.ENTÊTE.SE_CONNECTER}
                          href="/"
                        >
                          <span className="fr-btn fr-icon-user-fill">{i18n.ENTÊTE.SE_CONNECTER}</span>
                        </LienInterne>
                      </li>
                    )}
                  </ul>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
      {!router.location.pathname.includes(PARCOURS_INSCRIPTION_PATH) && <Navigation />}
    </header>
  );
};

export default Entête;
