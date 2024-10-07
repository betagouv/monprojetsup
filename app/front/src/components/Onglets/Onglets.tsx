import { type OngletsProps } from "./Onglets.interface";
import { useEffect, useId, useState } from "react";

const Onglets = ({ onglets, ongletAffichéParDéfaut = 0, nomAccessible }: OngletsProps) => {
  const [numéroOngletAffiché, setNuméroOngletAffiché] = useState(ongletAffichéParDéfaut);

  const id = useId();

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
                aria-controls={`tabpanel-${index}-panel-${id}`}
                aria-selected={estSélectionné}
                className="fr-tabs__tab"
                id={`tabpanel-${index}-${id}`}
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
            aria-labelledby={`tabpanel-${index}-${id}`}
            className={`fr-tabs__panel border border-solid border-[--border-default-grey] bg-white transition-none lg:px-10 lg:py-10 ${estSélectionné ? "fr-tabs__panel--selected" : ""}`}
            id={`tabpanel-${index}-panel-${id}`}
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
