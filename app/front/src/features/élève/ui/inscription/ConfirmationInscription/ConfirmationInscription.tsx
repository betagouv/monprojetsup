import confirmationSVG from "@/assets/confirmation.svg";
import BoutonSquelette from "@/components/_dsfr/BoutonSquelette/BoutonSquelette";
import Head from "@/components/_layout/Head/Head";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";

const ConfirmationInscription = () => {
  return (
    <>
      <Head title={i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.TITRE_PAGE} />
      <div className="fr-py-3w fr-px-2w fr-py-md-6w fr-px-md-8w text-center">
        <img
          alt="MPS"
          className="mb-4 w-[140px]"
          src={confirmationSVG}
        />
        <div className="*:mb-4">
          <Titre
            niveauDeTitre="h1"
            styleDeTitre="display--xs"
          >
            {i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.TITRE}
          </Titre>
        </div>
        <p className="fr-h3 mb-4">{i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.SOUS_TITRE}</p>
        <p className="fr-text--lead mb-10">{i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.CONTENU}</p>
        <LienInterne
          ariaLabel={i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.BOUTON_ACTION}
          href="/eleve/tableau-de-bord"
          variante="neutre"
        >
          <BoutonSquelette
            icône={{ position: "droite", classe: "fr-icon-arrow-right-line" }}
            label={i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.BOUTON_ACTION}
            taille="grand"
          />
        </LienInterne>
        <hr className="mt-10 pb-10" />
        <div className="bg-[--background-alt-beige-gris-galet]">
          <div className="bg-[url('/images-de-fond/bandeau-confirmation-inscription.svg')]  bg-no-repeat px-4 py-10 md:px-20">
            <div className="*:mb-4">
              <Titre
                niveauDeTitre="h2"
                styleDeTitre="h3"
              >
                {i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.BANDEAU.TITRE}
              </Titre>
            </div>
            <p className="fr-text--lead mb-0">{i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.BANDEAU.CONTENU}</p>
          </div>
        </div>
      </div>
    </>
  );
};

export default ConfirmationInscription;
