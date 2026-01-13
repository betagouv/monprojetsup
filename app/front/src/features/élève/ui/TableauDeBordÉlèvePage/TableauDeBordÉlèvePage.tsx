import useTableauDeBordÉlèvePage from "./useTableauDeBordÉlèvePage";
import Head from "@/components/_layout/Head/Head";
import Carte from "@/components/Carte/Carte.tsx";
import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n";
import ModaleParcoursupEvent from "@/features/commune/ui/ModaleParcoursupEvent/ModaleParcoursupEvent.tsx";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève.ts";
import CarteAvisÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteAvisÉlève/CarteAvisÉlève";
import CarteParcourSupÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CarteParcourSupÉlève/CarteParcourSupÉlève";
import CartePrimaireTableauDeBordÉlève from "@/features/élève/ui/TableauDeBordÉlèvePage/CartePrimaireTableauDeBordÉlève/CartePrimaireTableauDeBordÉlève";
import { createModal } from "@codegouvfr/react-dsfr/Modal";
import { Table } from "@codegouvfr/react-dsfr/Table";
import { useMemo } from "react";
import { Fragment } from "react/jsx-runtime";

const TableauDeBordÉlèvePage = () => {
  const { cartes, associationParcoursupPossible, estAuthentifié } = useTableauDeBordÉlèvePage();

  const messageBienvenue = estAuthentifié
    ? i18n.ÉLÈVE.TABLEAU_DE_BORD.MESSAGE_BIENVENUE.CONNECTE
    : i18n.ÉLÈVE.TABLEAU_DE_BORD.MESSAGE_BIENVENUE.DECONNECTE;

  const eleve = useÉlève();
  const modaleParcoursup = useMemo(
    () =>
      createModal({
        id: "modale-parcoursup-event",
        isOpenedByDefault: estAuthentifié && eleve.élève?.classe === "terminale",
      }),
    [estAuthentifié, eleve.élève?.classe],
  );

  return (
    <>
      <Head titre={i18n.PAGE_TABLEAU_DE_BORD.TITRE_PAGE} />
      <div className="h-full bg-[--background-alt-beige-gris-galet] bg-right-top bg-no-repeat lg:bg-[url('/images-de-fond/tableau-de-bord.svg')]">
        <div className="fr-container pb-20 pt-12">
          <h1 className="fr-h1 mb-3">{messageBienvenue}</h1>

          <p className="*:mb-2 *:font-normal *:text-[--text-mention-grey]">
            {i18n.ÉLÈVE.TABLEAU_DE_BORD.MESSAGE_BIENVENUE.SOUS_TITRE}
          </p>

          <ul className="grid list-none grid-cols-3 gap-6 p-0 md:grid-cols-12">
            <li className="col-span-3 md:col-span-7">
              <div className="fr-responsive-vid">
                <iframe
                  className="fr-responsive-vid__player"
                  sandbox="allow-scripts"
                  src="https://videos.onisep.fr/embed/media/2e79baf6-1bf4-4ba8-8377-554dad1170f6/"
                />
              </div>
            </li>

            <li className="col-span-3 md:col-span-5">
              <Carte
                auClicHandler={async () => {}}
                estFavori={false}
                estMasqué={false}
                id="monprojetsup"
                sélectionnée={false}
                titre=""
              >
                <h2 className="fr-h4 fr-mt-n2w">
                  MonProjetSup,
                  <br />
                  comment ça marche ?
                </h2>

                <p className="fr-my-n4w">Les 3 étapes pour compléter MonProjetSup</p>

                <div className="fr-my-n2w">
                  <Table
                    data={[
                      [
                        <span
                          className="font-bold text-[--warning-425-625-hover]"
                          key="step1"
                        >
                          01
                        </span>,
                        "Je complète mon profil 😎",
                      ],
                      [
                        <span
                          className="font-bold text-[--text-active-blue-france]"
                          key="step2"
                        >
                          02
                        </span>,
                        <span key="step2_description">
                          J’explore et <b>ajoute en favoris</b> des formations ❤️
                        </span>,
                      ],
                      [
                        <span
                          className="font-bold text-[--green-emeraude-925-125-active]"
                          key="step3"
                        >
                          03
                        </span>,
                        <span key="step3_description">
                          J’indique mon <b>niveau d’ambition</b> pour chaque formation 🤔
                        </span>,
                      ],
                    ]}
                    id="etapes-monprojetsup"
                    style={{ borderTop: "1.2px solid #929292" }}
                  />
                </div>
              </Carte>
            </li>
          </ul>

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

      <ModaleParcoursupEvent modale={modaleParcoursup} />
    </>
  );
};

export default TableauDeBordÉlèvePage;
