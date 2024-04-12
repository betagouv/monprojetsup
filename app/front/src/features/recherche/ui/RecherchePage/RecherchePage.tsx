import Head from "@/components/_layout/Head/Head";
import { i18n } from "@/configuration/i18n/i18n";
import CarteFormation from "@/features/formation/ui/CarteFormation/CarteFormation";
import CarteMétier from "@/features/métier/ui/CarteMétier/CarteMétier";
import { rechercheQueryOptions } from "@/features/recherche/ui/options";
import { useSuspenseQuery } from "@tanstack/react-query";
import { useMemo } from "react";

const RecherchePage = () => {
  const {
    data: { formations, métiers },
  } = useSuspenseQuery(rechercheQueryOptions);

  const résultatsAlternés = useMemo(() => {
    const tableau = [];
    for (let index = 0; index < Math.max(formations.length, métiers.length); index++) {
      if (formations[index] !== undefined) tableau.push({ ...formations[index], type: "formation" } as const);
      if (métiers[index] !== undefined) tableau.push({ ...métiers[index], type: "métier" } as const);
    }

    return tableau;
  }, [formations, métiers]);

  return (
    <>
      <Head title={i18n.PAGE_RECHERCHE.TITRE} />
      <div className="bg-gradient-to-r from-[--background-contrast-beige-gris-galet] from-70% to-white to-70%">
        <div className="fr-container">
          <div className="grid grid-flow-col grid-cols-[auto_1fr] justify-start">
            <ul className="m-0 grid h-[calc(100vh-250px)] list-none grid-flow-row gap-6 overflow-auto py-4 pl-0 pr-4 lg:pr-10">
              {résultatsAlternés.map((résultat) => (
                <li key={`${résultat.type}-${résultat.id}`}>
                  {résultat.type === "formation" ? (
                    <CarteFormation
                      affinité={résultat.affinité}
                      id={résultat.id}
                      métiersAccessibles={résultat.métiersAccessibles}
                      nom={résultat.nom}
                      sélectionnée={résultat.id === "1"}
                    />
                  ) : (
                    <CarteMétier
                      formations={résultat.formations}
                      id={résultat.id}
                      nom={résultat.nom}
                    />
                  )}
                </li>
              ))}
            </ul>
            <div className="border-blue border border-solid bg-white px-4 py-6 md:min-w-[500px] md:px-10 md:py-12 lg:min-w-[610px] lg:px-20 lg:py-12">
              Contenu du détail
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default RecherchePage;
