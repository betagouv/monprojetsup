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
      <div className="grid grid-flow-col">
        <ul className="bg-[--background-contrast-beige-gris-galet] list-none fr-p-2w fr-m-0 grid grid-flow-row gap-6">
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
        <div className="bg-white fr-px-10w fr-py-5w">Contenu du détail</div>
      </div>
    </>
  );
};

export default RecherchePage;
