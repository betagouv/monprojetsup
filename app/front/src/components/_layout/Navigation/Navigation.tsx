import { i18n } from "@/configuration/i18n/i18n";
import { Link } from "@tanstack/react-router";

const Navigation = () => {
  const pages = [
    {
      title: i18n.PAGE_ACCUEIL.TITLE,
      to: "/",
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
          title={i18n.COMMUN.CLOSE}
          type="button"
        >
          {i18n.COMMUN.CLOSE}
        </button>
        <div className="fr-header__menu-links" />
        <nav
          aria-label={i18n.NAVIGATION.MAIN_NAVIGATION}
          className="fr-nav"
          id="main-navigation"
          role="navigation"
        >
          <ul className="fr-nav__list">
            {pages.map((page) => (
              <li
                className="fr-nav__item"
                key={page.to}
              >
                <Link
                  activeProps={{ "aria-current": "page" }}
                  className="fr-nav__link"
                  target="_self"
                  to={page.to}
                >
                  {page.title}
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
