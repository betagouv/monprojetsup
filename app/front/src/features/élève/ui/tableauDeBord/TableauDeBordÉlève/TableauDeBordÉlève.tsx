import explorerSVG from "@/assets/explorer.svg";
import favorisSVG from "@/assets/favoris.svg";
import profilSVG from "@/assets/profil.svg";
import Head from "@/components/_layout/Head/Head";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import CarteTableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/CarteTableauDeBordÉlève/CarteTableauDeBordÉlève";
import { type CarteTableauDeBordÉlèveProps } from "@/features/élève/ui/tableauDeBord/CarteTableauDeBordÉlève/CarteTableauDeBordÉlève.interface";
import TémoignageTableauDeBordÉlève from "@/features/élève/ui/tableauDeBord/TémoignageTableauDeBordÉlève/TémoignageTableauDeBordÉlève";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { useMemo } from "react";
import { Fragment } from "react/jsx-runtime";

const TableauDeBordÉlève = () => {
  useSuspenseQuery(élèveQueryOptions);
  const { data: élève } = useQuery(élèveQueryOptions);

  const cartes: CarteTableauDeBordÉlèveProps[] = [
    {
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.SOUS_TITRE,
      illustration: explorerSVG,
      lien: "/",
    },
    {
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.SOUS_TITRE,
      illustration: favorisSVG,
      lien: "/",
    },
    {
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.SOUS_TITRE,
      illustration: profilSVG,
      lien: "/profil",
    },
  ];

  const témoignage = useMemo(() => {
    let contenu: string;

    if (élève?.classe === "terminale") {
      contenu = i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.TERMINALE;
    } else if (élève?.classe === "premiere") {
      contenu = i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.PREMIÈRE;
    } else {
      contenu = i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.SECONDE;
    }

    return {
      contenu,
      auteur: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.AUTEUR,
      rôle: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.RÔLE,
    };
  }, [élève]);

  return (
    <>
      <Head title={i18n.PAGE_TABLEAU_DE_BORD.TITRE_PAGE} />
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
        </div>
      </div>
    </>
  );
};

export default TableauDeBordÉlève;
