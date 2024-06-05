import profilSVG from "@/assets/profil.svg";
import FilAriane from "@/components/_dsfr/FilAriane/FilAriane";
import Onglets from "@/components/_dsfr/Onglets/Onglets";
import Head from "@/components/_layout/Head/Head";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import ModifierProfilÉlèveForm from "@/features/élève/ui/profil/ModifierProfilÉlèveForm/ModifierProfilÉlèveForm";
import { useSuspenseQuery } from "@tanstack/react-query";

const ProfilÉlèvePage = () => {
  useSuspenseQuery(élèveQueryOptions);
  const élève = queryClient.getQueryData(élèveQueryOptions.queryKey);

  return (
    <>
      <Head title={i18n.ÉLÈVE.PROFIL.TITRE} />
      <div className="h-full bg-[--background-alt-beige-gris-galet] bg-right-top bg-no-repeat lg:bg-[url('/images-de-fond/tableau-de-bord.svg')]">
        <div className="fr-container h-full   pb-20  pt-4">
          <FilAriane
            chemin={[{ nom: i18n.ÉLÈVE.TABLEAU_DE_BORD.TITRE_PAGE, lien: "/eleve/tableau-de-bord" }]}
            libelléPageCourante={i18n.ÉLÈVE.PROFIL.TITRE}
          />
          <div className="*:mb-12">
            <Titre
              niveauDeTitre="h1"
              styleDeTitre="h3"
            >
              {i18n.ÉLÈVE.PROFIL.TITRE}
            </Titre>
          </div>
          <div className="grid gap-6 lg:grid-flow-col">
            <div className="grid content-start justify-items-center border border-solid border-[--border-default-grey] bg-white px-10 py-8 lg:w-[18rem]">
              <img
                alt=""
                className="w-[104px] self-center"
                src={profilSVG}
              />
              <p className="fr-h3 mb-1 text-[--text-active-grey]">
                {élève?.prénom} {élève?.nom}
              </p>
              <p className="fr-text text-[--text-mention-grey]">{élève?.email ?? élève?.nomUtilisateur}</p>
            </div>
            <Onglets
              nomAccessible={i18n.ÉLÈVE.PROFIL.TITRE}
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
                  titre: i18n.ÉLÈVE.INTÊRETS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
                  contenu: (
                    <ModifierProfilÉlèveForm
                      formulaireId="intêrets"
                      titre={i18n.ÉLÈVE.INTÊRETS.PARCOURS_INSCRIPTION.TITRE}
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
              ]}
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default ProfilÉlèvePage;
