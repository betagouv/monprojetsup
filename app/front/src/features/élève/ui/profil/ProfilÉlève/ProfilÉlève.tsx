import profilSVG from "@/assets/profil.svg";
import Head from "@/components/_layout/Head/Head";
import Bouton from "@/components/Bouton/Bouton";
import FilAriane from "@/components/FilAriane/FilAriane";
import Onglets from "@/components/Onglets/Onglets";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import FormationsMasquées from "@/features/élève/ui/profil/FormationsMasquées/FormationsMasquées";
import ModifierProfilÉlèveForm from "@/features/élève/ui/profil/ModifierProfilÉlèveForm/ModifierProfilÉlèveForm";
import { useRouterState } from "@tanstack/react-router";

const ProfilÉlève = () => {
  const utilisateur = useUtilisateur();
  const router = useRouterState();

  const indexOngletÀActiver = () => {
    switch (router.location.hash) {
      case "domaines":
        return 1;
      case "interets":
        return 2;
      case "etude":
        return 3;
      case "formations-masquees":
        return 4;
      default:
        return 0;
    }
  };

  return (
    <>
      <Head titre={i18n.PAGE_PROFIL.TITRE_PAGE} />
      <div className="h-full bg-[--background-alt-beige-gris-galet] bg-right-top bg-no-repeat lg:bg-[url('/images-de-fond/tableau-de-bord.svg')]">
        <div className="fr-container h-full pb-20 pt-4">
          <FilAriane
            chemin={[{ nom: i18n.PAGE_TABLEAU_DE_BORD.TITRE_PAGE, lien: "/" }]}
            libelléPageCourante={i18n.PAGE_PROFIL.TITRE_PAGE}
          />
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
                <br />
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
            <Onglets
              nomAccessible={i18n.PAGE_PROFIL.TITRE}
              ongletAffichéParDéfaut={indexOngletÀActiver()}
              onglets={[
                {
                  titre: i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  contenu: (
                    <ModifierProfilÉlèveForm
                      formulaireId="scolarité"
                      titre={i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                },
                {
                  titre: i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  contenu: (
                    <ModifierProfilÉlèveForm
                      formulaireId="domaines"
                      titre={i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                },
                {
                  titre: i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  contenu: (
                    <ModifierProfilÉlèveForm
                      formulaireId="intérêts"
                      titre={i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                },
                {
                  titre: i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  contenu: (
                    <ModifierProfilÉlèveForm
                      formulaireId="étude"
                      titre={i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE}
                    />
                  ),
                },
                {
                  titre: i18n.FORMATIONS_MASQUÉES.TITRE,
                  contenu: <FormationsMasquées />,
                },
              ]}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default ProfilÉlève;
