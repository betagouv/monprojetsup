import profilSVG from "@/assets/profil.svg";
import Head from "@/components/_layout/Head/Head";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import ModifierProfilÉlèveForm from "@/features/élève/ui/ProfilÉlèvePage/ModifierProfilÉlèveForm/ModifierProfilÉlèveForm";
import useUtilisateur from "@/features/utilisateur/ui/useUtilisateur";
import { Tabs } from "@codegouvfr/react-dsfr/Tabs";
import { useRouterState } from "@tanstack/react-router";

const ProfilÉlèvePage = () => {
  const utilisateur = useUtilisateur();
  const router = useRouterState();

  return (
    <>
      <Head titre={i18n.PAGE_PROFIL.TITRE_PAGE} />
      <div className="h-full bg-[--background-alt-beige-gris-galet] bg-right-top bg-no-repeat lg:bg-[url('/images-de-fond/tableau-de-bord.svg')]">
        <div className="fr-container h-full pb-20 pt-12">
          <div className="*:mb-12">
            <Titre
              niveauDeTitre="h1"
              styleDeTitre="h3"
            >
              {i18n.PAGE_PROFIL.TITRE}
            </Titre>
          </div>
          <div className="grid gap-6 lg:grid-flow-col">
            <div className="grid content-start justify-items-center border border-solid border-[--border-default-grey] bg-white px-10 py-8 lg:w-[18rem]">
              <img
                alt=""
                className="w-[104px] self-center"
                src={profilSVG}
              />
              <p className="fr-h3 mb-1 w-full overflow-hidden text-ellipsis whitespace-nowrap text-center text-[--text-active-grey]">
                {utilisateur.prénom}
                <br role="presentation" />
                {utilisateur.nom}
              </p>
              {utilisateur.email && (
                <p className="fr-text mb-1 break-all text-center text-[--text-mention-grey]">{utilisateur.email}</p>
              )}
              <Bouton
                auClic={async () => await utilisateur.seDéconnecter()}
                label={i18n.PAGE_PROFIL.SE_DÉCONNECTER}
                type="button"
                variante="quaternaire"
              />
            </div>
            <Tabs
              label={i18n.PAGE_PROFIL.TITRE}
              tabs={[
                {
                  label: i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  content: (
                    <ModifierProfilÉlèveForm
                      formulaireId="scolarité"
                      titre={i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                  isDefault: !router.location.hash,
                },
                {
                  label: i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  content: (
                    <ModifierProfilÉlèveForm
                      formulaireId="domaines"
                      titre={i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                  isDefault: router.location.hash === "domaines",
                },
                {
                  label: i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  content: (
                    <ModifierProfilÉlèveForm
                      formulaireId="intérêts"
                      titre={i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                  isDefault: router.location.hash === "interets",
                },
                {
                  label: i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  content: (
                    <ModifierProfilÉlèveForm
                      formulaireId="étude"
                      titre={i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                  isDefault: router.location.hash === "etude",
                },
                {
                  label: i18n.PAGE_PROFIL.MÉTIERS.TITRE_ONGLET,
                  content: (
                    <ModifierProfilÉlèveForm
                      formulaireId="métiers"
                      titre={i18n.ÉLÈVE.MÉTIERS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE}
                    />
                  ),
                  isDefault: router.location.hash === "metiers",
                },
              ]}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default ProfilÉlèvePage;
