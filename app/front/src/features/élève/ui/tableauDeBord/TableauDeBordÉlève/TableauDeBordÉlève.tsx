import useTableauDeBordÉlève from "./useTableauDeBordÉlève";
import Head from "@/components/_layout/Head/Head";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import CarteAvisÉlève from "@/features/élève/ui/tableauDeBord/CarteAvisÉlève/CarteAvisÉlève";
import CarteTableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/CarteTableauDeBordÉlève/CarteTableauDeBordÉlève";
import TémoignageTableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/TémoignageTableauDeBordÉlève/TémoignageTableauDeBordÉlève";
import { Fragment } from "react/jsx-runtime";

const TableauDeBordÉlève = () => {
  const { cartes, témoignage } = useTableauDeBordÉlève();

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
              {i18n.ÉLÈVE.TABLEAU_DE_BORD.TITRE}
            </Titre>
          </div>
          <p className="fr-h1 mb-10">{i18n.ÉLÈVE.TABLEAU_DE_BORD.MESSAGE_BIENVENUE}</p>
          <ul className="grid list-none grid-cols-1 gap-6 p-0 md:grid-cols-2">
            {cartes.map((carte, index) => (
              <Fragment key={carte.lien}>
                <li>
                  <CarteTableauDeBordÉlève
                    illustration={carte.illustration}
                    lien={carte.lien}
                    sousTitre={carte.sousTitre}
                    titre={carte.titre}
                  />
                </li>
                {index === 1 && (
                  <li>
                    <TémoignageTableauDeBordÉlève
                      auteur={témoignage.auteur}
                      contenu={témoignage.contenu}
                      rôle={témoignage.rôle}
                    />
                  </li>
                )}
              </Fragment>
            ))}
          </ul>
          <hr className="mb-4 mt-10" />
          <CarteAvisÉlève />
        </div>
      </div>
    </>
  );
};

export default TableauDeBordÉlève;
