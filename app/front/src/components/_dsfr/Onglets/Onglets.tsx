import { type OngletsProps } from "./Onglets.interface";
import { useEffect, useState } from "react";

const Onglets = ({ onglets, ongletAffichéParDéfaut = 0, nomAccessible }: OngletsProps) => {
  const [numéroOngletAffiché, setNuméroOngletAffiché] = useState(ongletAffichéParDéfaut);

  useEffect(() => {
    setNuméroOngletAffiché(ongletAffichéParDéfaut);
  }, [ongletAffichéParDéfaut]);

  const handleTabChange = (tabIndex: number) => {
    if (numéroOngletAffiché !== tabIndex) {
      setNuméroOngletAffiché(tabIndex);
    }
  };

  return (
    <div className="fr-tabs shadow-none before:shadow-none before:content-['']">
      <ul
        aria-label={nomAccessible}
        className="fr-tabs__list"
        role="tablist"
      >
        {onglets.map((onglet, index) => {
          const estSélectionné = index === numéroOngletAffiché;

          return (
            <li
              key={`tabLabel-${onglet.titre}`}
              role="presentation"
            >
              <button
                aria-controls={`tabpanel-${index}-panel`}
                aria-selected={estSélectionné}
                className="fr-tabs__tab"
                id={`tabpanel-${index}`}
                onClick={() => {
                  handleTabChange(index);
                }}
                role="tab"
                tabIndex={estSélectionné ? 0 : -1}
                type="button"
              >
                {onglet.titre}
              </button>
            </li>
          );
        })}
      </ul>
      {onglets.map((onglet, index) => {
        const estSélectionné = index === numéroOngletAffiché;

        return (
          <div
            aria-labelledby={`tabpanel-${index}`}
            className={`fr-tabs__panel border border-solid border-[--border-default-grey] bg-white transition-none lg:px-[4.5rem] lg:py-14 ${estSélectionné ? "fr-tabs__panel--selected" : ""}`}
            id={`tabpanel-${index}-panel`}
            key={`tabpanel-${onglet.titre}`}
            role="tabpanel"
            tabIndex={estSélectionné ? 0 : -1}
          >
            {onglet.contenu}
          </div>
        );
      })}
    </div>
  );
};

export default Onglets;
