import profilSVG from "@/assets/profil.svg";
import FilAriane from "@/components/_dsfr/FilAriane/FilAriane";
import Head from "@/components/_layout/Head/Head";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";

const ProfilEnseignant = () => {
  const utilisateur = useUtilisateur();

  return (
    <>
      <Head title={i18n.PAGE_PROFIL.TITRE_PAGE} />
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
              <p className="fr-text mb-1 break-all text-center text-[--text-mention-grey]">{utilisateur.email}</p>
              <Bouton
                auClic={() => utilisateur.seDéconnecter()}
                label={i18n.PAGE_PROFIL.SE_DÉCONNECTER}
                type="button"
                variante="quaternaire"
              />
            </div>
            <div>Profil enseignant</div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProfilEnseignant;
