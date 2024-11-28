import useVoeux from "./useVoeux";
import { type VoeuxProps } from "./Voeux.interface";
import VoeuxOnglet from "./VoeuxOnglet/VoeuxOnglet";
import VoeuxOngletToutesLesCommunes from "./VoeuxOngletToutesLesCommunes/VoeuxOngletToutesLesCommunes";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import { i18n } from "@/configuration/i18n/i18n";
import { trierTableauDObjetsParOrdreAlphabétique } from "@/utils/array";
import { Tabs } from "@codegouvfr/react-dsfr/Tabs";

const Voeux = ({ formation }: VoeuxProps) => {
  const { communesFavorites } = useVoeux({ formation });

  const ongletsParCommuneFavorite = trierTableauDObjetsParOrdreAlphabétique(communesFavorites ?? [], "nom").map(
    (communeFavorite) => ({
      label: communeFavorite.nom,
      content: (
        <VoeuxOnglet
          codeCommune={communeFavorite.codeInsee}
          formation={formation}
        />
      ),
    }),
  );

  const ongletToutesLesCommunes = {
    label: i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.TITRE_ONGLET,
    content: <VoeuxOngletToutesLesCommunes formation={formation} />,
  };

  return (
    <>
      <div>
        <p className="mb-0 font-medium text-[--text-label-grey]">{i18n.PAGE_FORMATION.CHOIX.VOEUX.LÉGENDE}</p>
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
          {formation.lienParcoursSup && (
            <LienExterne
              ariaLabel={i18n.PAGE_FORMATION.CHOIX.VOEUX.LIENS.PARCOURSUP}
              href={formation.lienParcoursSup}
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
