import useVoeux from "./useVoeux";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n";
import { Tabs } from "@codegouvfr/react-dsfr/Tabs";

const Voeux = () => {
  const { ongletToutesLesCommunes, ongletsParCommuneFavorite, lienParcoursup } = useVoeux();

  return (
    <>
      <div>
        <div className="*:mb-0">
          <Titre
            niveauDeTitre="h3"
            styleDeTitre="text--lg"
          >
            {i18n.PAGE_FORMATION.CHOIX.VOEUX.TITRE}
          </Titre>
        </div>
        <div className="ml-1 grid grid-flow-col justify-start gap-6">
          <LienInterne
            ariaLabel={i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PRÉFÉRENCES}
            hash="etude"
            href="/profil"
            icône={{ classe: "fr-icon-equalizer-line", position: "gauche" }}
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PRÉFÉRENCES}
          </LienInterne>
          {lienParcoursup && (
            <LienExterne
              ariaLabel={i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PARCOURSUP}
              href={lienParcoursup}
              taille="petit"
              variante="simple"
            >
              {i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PARCOURSUP}
            </LienExterne>
          )}
        </div>
      </div>
      <Tabs
        label={i18n.ACCESSIBILITÉ.ONGLETS_VOEUX}
        tabs={[...ongletsParCommuneFavorite, ongletToutesLesCommunes]}
      />
    </>
  );
};

export default Voeux;
