import useTableauDeBordÉlèvePage from "./useTableauDeBordÉlèvePage";
import Head from "@/components/_layout/Head/Head";
import Titre from "@/components/Titre/Titre";
import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n";
import CarteAvisÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteAvisÉlève/CarteAvisÉlève";
import CarteParcourSupÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteParcourSupÉlève/CarteParcourSupÉlève";
import CartePrimaireTableauDeBordÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CartePrimaireTableauDeBordÉlève/CartePrimaireTableauDeBordÉlève";
import { Fragment } from "react/jsx-runtime";

const TableauDeBordÉlèvePage = () => {
  const { cartes, associationParcoursupPossible, estAuthentifié } = useTableauDeBordÉlèvePage();

  const messageTitre = estAuthentifié
    ? i18n.ÉLÈVE.TABLEAU_DE_BORD.TITRE_CONNECTE
    : i18n.ÉLÈVE.TABLEAU_DE_BORD.TITRE_DECONNECTE;
  const messageBienvenue = estAuthentifié
    ? i18n.ÉLÈVE.TABLEAU_DE_BORD.MESSAGE_BIENVENUE_CONNECTE
    : i18n.ÉLÈVE.TABLEAU_DE_BORD.MESSAGE_BIENVENUE_DECONNECTE;
  return (
    <>
      <Head titre={i18n.PAGE_TABLEAU_DE_BORD.TITRE_PAGE} />
      <div className="h-full bg-[--background-alt-beige-gris-galet] bg-right-top bg-no-repeat lg:bg-[url('/images-de-fond/tableau-de-bord.svg')]">
        <div className="fr-container pb-20 pt-12">
          <div className="*:mb-2 *:font-normal *:text-[--text-mention-grey]">
            <Titre
              niveauDeTitre="h1"
              styleDeTitre="text--sm"
            >
              {messageTitre}
            </Titre>
          </div>
          <p className="fr-h1 mb-10">{messageBienvenue}</p>
          <ul className="grid list-none grid-cols-1 gap-6 p-0 md:grid-cols-2">
            {cartes.map((carte) => (
              <Fragment key={carte.lien}>
                <li>
                  <CartePrimaireTableauDeBordÉlève
                    illustration={carte.illustration}
                    lien={carte.lien}
                    sousTitre={carte.sousTitre}
                    titre={carte.titre}
                  />
                </li>
              </Fragment>
            ))}
          </ul>
          <ul
            className={`grid list-none grid-cols-1 gap-6 p-0 ${associationParcoursupPossible ? "md:grid-cols-2" : ""} `}
          >
            {associationParcoursupPossible && (
              <li>
                <CarteParcourSupÉlève />
              </li>
            )}
            {environnement.VITE_LAISSER_AVIS_URL && (
              <li>
                <CarteAvisÉlève />
              </li>
            )}
          </ul>
        </div>
      </div>
    </>
  );
};

export default TableauDeBordÉlèvePage;
