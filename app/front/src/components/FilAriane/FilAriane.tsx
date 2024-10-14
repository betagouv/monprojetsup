import { FilArianeProps } from "./FilAriane.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { Link } from "@tanstack/react-router";
import { useId, useState } from "react";

const FilAriane = ({ chemin, libelléPageCourante }: FilArianeProps) => {
  const [estOuvert, setEstOuvert] = useState(false);
  const id = useId();

  return (
    <nav
      aria-label={i18n.ACCESSIBILITÉ.VOUS_ÊTES_ICI}
      className="fr-breadcrumb mb-2"
      role="navigation"
    >
      <button
        aria-controls={`breadcrumb-${id}`}
        aria-expanded="false"
        className="fr-breadcrumb__button"
        onClick={() => setEstOuvert(!estOuvert)}
        type="button"
      >
        {i18n.ACCESSIBILITÉ.VOIR_FIL_ARIANE}
      </button>
      <div
        className={estOuvert ? "fr-collapse--expanded" : "fr-collapse"}
        id={`breadcrumb-${id}`}
      >
        <ol className="fr-breadcrumb__list">
          {chemin
            ? chemin.map((page) => (
                <li key={page.nom}>
                  <Link
                    className="fr-breadcrumb__link"
                    to={page.lien}
                  >
                    {page.nom}
                  </Link>
                </li>
              ))
            : null}
          <li>
            <span
              aria-current="page"
              className="fr-breadcrumb__link"
            >
              {libelléPageCourante}
            </span>
          </li>
        </ol>
      </div>
    </nav>
  );
};

export default FilAriane;
