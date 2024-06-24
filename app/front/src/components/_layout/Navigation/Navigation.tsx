import { i18n } from "@/configuration/i18n/i18n";
import { type router } from "@/configuration/lib/tanstack-router";
import { Link } from "@tanstack/react-router";

const Navigation = () => {
  const pages: Array<{ titre: string; href: keyof (typeof router)["routesByPath"] }> = [
    {
      titre: i18n.PAGE_ACCUEIL.TITLE,
      href: "/",
    },
    {
      titre: "Inscription",
      href: "/eleve/inscription/projet",
    },
  ];

  return (
    <div
      aria-labelledby="navigation-burger-button"
      className="fr-header__menu fr-modal"
      id="navigation-modal"
    >
      <div className="fr-container">
        <button
          aria-controls="navigation-modal"
          className="fr-btn--close fr-btn"
          title={i18n.COMMUN.FERMER}
          type="button"
        >
          {i18n.COMMUN.FERMER}
        </button>
        <div className="fr-header__menu-links" />
        <nav
          aria-label={i18n.ACCESSIBILITÉ.MENU_PRINCIPAL}
          className="fr-nav"
          id="main-navigation"
          role="navigation"
        >
          <ul className="fr-nav__list">
            {pages.map((page) => (
              <li
                className="fr-nav__item"
                key={page.href}
              >
                <Link
                  activeProps={{ "aria-current": "page" }}
                  className="fr-nav__link"
                  target="_self"
                  to={page.href}
                >
                  {page.titre}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      </div>
    </div>
  );
};

export default Navigation;
