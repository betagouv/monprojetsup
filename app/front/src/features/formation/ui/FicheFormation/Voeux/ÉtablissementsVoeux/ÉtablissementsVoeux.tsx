import ÉtablissemenentsVoeuxOngletToutesLesVilles from "./ÉtablissemenentsVoeuxOngletToutesLesVilles/ÉtablissemenentsVoeuxOngletToutesLesVilles";
import { type ÉtablissementsVoeuxProps } from "./ÉtablissementsVoeux.interface";
import ÉtablissementsVoeuxOnglet from "./ÉtablissementsVoeuxOnglet/ÉtablissementsVoeuxOnglet";
import useÉtablissementsVoeux from "./useÉtablissementsVoeux";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Onglets from "@/components/Onglets/Onglets";
import { i18n } from "@/configuration/i18n/i18n";
import { trierTableauDObjetsParOrdreAlphabétique } from "@/utils/array";

const ÉtablissementsVoeux = ({ formation }: ÉtablissementsVoeuxProps) => {
  const { communesFavorites } = useÉtablissementsVoeux({ formation });

  const ongletsParCommuneFavorite = trierTableauDObjetsParOrdreAlphabétique(communesFavorites ?? [], "nom").map(
    (communeFavorite) => ({
      titre: communeFavorite.nom,
      contenu: (
        <ÉtablissementsVoeuxOnglet
          codeCommune={communeFavorite.codeInsee}
          formation={formation}
        />
      ),
    }),
  );

  const ongletToutesLesCommunes = {
    titre: i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.TOUTES_LES_COMMUNES.TITRE_ONGLET,
    contenu: <ÉtablissemenentsVoeuxOngletToutesLesVilles formation={formation} />,
  };

  return (
    <>
      <div>
        <p className="mb-0 font-medium text-[--text-label-grey]">{i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LÉGENDE}</p>
        <div className="ml-1 grid grid-flow-col justify-start gap-6">
          <LienInterne
            ariaLabel={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
            href="/profil"
            icône={{ classe: "fr-icon-equalizer-line", position: "gauche" }}
            taille="petit"
            variante="simple"
          >
            {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PRÉFÉRENCES}
          </LienInterne>
          {formation.lienParcoursSup && (
            <LienExterne
              ariaLabel={i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PARCOURSUP}
              href={formation.lienParcoursSup}
              taille="petit"
              variante="simple"
            >
              {i18n.PAGE_FORMATION.VOEUX.ÉTABLISSEMENTS.LIENS.PARCOURSUP}
            </LienExterne>
          )}
        </div>
      </div>
      <Onglets
        nomAccessible={i18n.ACCESSIBILITÉ.ONGLETS_VOEUX_ÉTABLISSEMENTS}
        onglets={[...ongletsParCommuneFavorite, ongletToutesLesCommunes]}
      />
    </>
  );
};

export default ÉtablissementsVoeux;
