import logoMPS from "@/assets/logo/logo-mps.svg";
import { i18n } from "@/configuration/i18n/i18n";
import { Link } from "@tanstack/react-router";

const PiedDePage = () => {
  const liensOfficielsExternes = [
    {
      url: "https://legifrance.gouv.fr",
      intitulé: "legifrance.gouv.fr",
    },
    {
      url: "https://gouvernement.fr",
      intitulé: "gouvernement.fr",
    },
    {
      url: "https://service-public.fr",
      intitulé: "service-public.fr",
    },
    {
      url: "https://data.gouv.fr",
      intitulé: "data.gouv.fr",
    },
  ];

  const liensInternes = [
    {
      url: "/",
      intitulé: i18n.PIED_DE_PAGE.LIENS_INTERNES.PLAN_DU_SITE,
    },
    {
      url: "/",
      intitulé: i18n.PIED_DE_PAGE.LIENS_INTERNES.ACCESSIBILITÉ,
    },
    {
      url: "/",
      intitulé: i18n.PIED_DE_PAGE.LIENS_INTERNES.MENTIONS_LÉGALES,
    },
    {
      url: "/",
      intitulé: i18n.PIED_DE_PAGE.LIENS_INTERNES.DONNÉES_PERSONNELLES,
    },
    {
      url: "/",
      intitulé: i18n.PIED_DE_PAGE.LIENS_INTERNES.GESTION_COOKIES,
    },
  ];

  return (
    <footer
      className="fr-footer"
      id="footer"
      role="contentinfo"
    >
      <div className="fr-container">
        <div className="fr-footer__body">
          <div className="fr-footer__brand fr-enlarge-link">
            <p className="fr-logo">
              Ministère
              <br />
              de l’enseignement
              <br />
              supérieur
              <br />
              et de la recherche
            </p>
            <Link
              className="fr-footer__brand-link"
              title={`Accueil - ${i18n.APP.NOM}`}
              to="/"
            >
              <img
                alt="MPS"
                className="fr-responsive-img"
                src={logoMPS}
              />
            </Link>
          </div>
          <div className="fr-footer__content">
            <p className="fr-footer__content-desc">{i18n.PIED_DE_PAGE.DESCRIPTION_SERVICE}</p>
            <ul className="fr-footer__content-list">
              {liensOfficielsExternes.map((lienExterne) => (
                <li
                  className="fr-footer__content-item"
                  key={lienExterne.url}
                >
                  <a
                    className="fr-footer__content-link"
                    href={lienExterne.url}
                    rel="noopener external noreferrer"
                    target="_blank"
                    title={`${lienExterne.intitulé} - nouvelle fenêtre`}
                  >
                    {lienExterne.intitulé}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </div>
        <div className="fr-footer__bottom">
          <ul className="fr-footer__bottom-list">
            {liensInternes.map((lienInterne) => (
              <li
                className="fr-footer__bottom-item"
                key={lienInterne.url}
              >
                <Link
                  className="fr-footer__bottom-link"
                  to={lienInterne.url}
                >
                  {lienInterne.intitulé}
                </Link>
              </li>
            ))}
          </ul>
          <div className="fr-footer__bottom-copy">
            <p>
              Sauf mention explicite de propriété intellectuelle détenue par des tiers, les contenus de ce site sont
              proposés sous{" "}
              <a
                href="https://github.com/etalab/licence-ouverte/blob/master/LO.md"
                rel="noopener external noreferrer"
                target="_blank"
                title="Voir la licence Etalab 2.0 - nouvelle fenêtre"
              >
                licence etalab-2.0
              </a>
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default PiedDePage;
