import logoMPS from "@/assets/logo/logo-mps.svg";
import Navigation from "@/components/_layout/Navigation/Navigation";
import Bouton from "@/components/Bouton/Bouton";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import { useRouterState } from "@tanstack/react-router";

const Entête = () => {
  const router = useRouterState();
  const utilisateur = useUtilisateur();

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
                    Française
                  </p>
                </div>
                <div className="fr-header__operator">
                  <img
                    alt="MPS"
                    className="fr-responsive-img"
                    src={logoMPS}
                  />
                </div>
                {!router.location.pathname.includes(constantes.ÉLÈVE.PATH_PARCOURS_INSCRIPTION) && (
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
            <div className="fr-header__tools">
              <div className="fr-header__tools-links">
                <ul className="fr-btns-group">
                  {router.location.pathname.includes(constantes.ÉLÈVE.PATH_PARCOURS_INSCRIPTION) ? (
                    <li>
                      <Bouton
                        auClic={async () => await utilisateur.seDéconnecter()}
                        icône={{ position: "gauche", classe: "fr-icon-close-line" }}
                        label={i18n.PAGE_PROFIL.SE_DÉCONNECTER}
                        type="button"
                        variante="quaternaire"
                      />
                    </li>
                  ) : (
                    <>
                      <li>
                        <LienExterne
                          ariaLabel={i18n.ENTÊTE.PLATEFORME_AVENIRS}
                          href={constantes.LIENS.AVENIRS}
                          icône={{ classe: "fr-icon-arrow-go-back-fill", position: "droite" }}
                        >
                          <span className="fr-btn fr-icon-arrow-go-back-fill">{i18n.ENTÊTE.PLATEFORME_AVENIRS}</span>
                        </LienExterne>
                      </li>
                      {utilisateur.id ? (
                        <li>
                          <LienInterne
                            ariaLabel={`${utilisateur.prénom} ${utilisateur.nom}`}
                            href="/profil"
                          >
                            <span className="fr-btn fr-icon-user-fill">{`${utilisateur.prénom} ${utilisateur.nom}`}</span>
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
                          w{" "}
                        </li>
                      )}
                    </>
                  )}
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
      {!router.location.pathname.includes(constantes.ÉLÈVE.PATH_PARCOURS_INSCRIPTION) && <Navigation />}
    </header>
  );
};

export default Entête;
