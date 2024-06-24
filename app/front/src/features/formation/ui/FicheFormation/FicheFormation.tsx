import { type FicheFormationProps } from "./FicheFormation.interface";
import MétiersAccessiblesFicheFormation from "./MétiersAccessiblesFicheFormation/MétiersAccessiblesFicheFormation";
import OngletCritèresFicheFormation from "./OngletCritèresFicheFormation/OngletCritèresFicheFormation";
import OngletFormationFicheFormation from "./OngletFormationFicheFormation/OngletFormationFicheFormation";
import Badge from "@/components/_dsfr/Badge/Badge";
import Onglets from "@/components/_dsfr/Onglets/Onglets";
import BoutonsFavorisCorbeille from "@/components/BoutonsFavorisCorbeille/BoutonsFavorisCorbeille";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import TexteTronqué from "@/components/TexteTronqué/TexteTronqué";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";

const FicheFormation = ({ formation }: FicheFormationProps) => {
  const générerLesOnglets = () => {
    const onglets = [];

    if ((formation.descriptifs.formation && formation.descriptifs.formation !== "") || formation.liens.length > 0) {
      onglets.push({
        titre: i18n.PAGE_FORMATION.ONGLET_FORMATION,
        contenu: (
          <OngletFormationFicheFormation
            liens={formation.liens}
            texte={formation.descriptifs.formation}
          />
        ),
      });
    }

    if (formation.descriptifs.détails && formation.descriptifs.détails !== "") {
      onglets.push({
        titre: i18n.PAGE_FORMATION.ONGLET_DÉTAILS,
        contenu: (
          <TexteTronqué
            nombreDeLigneÀAfficher="4"
            texte={formation.descriptifs.détails}
          />
        ),
      });
    }

    if (
      (formation.descriptifs.attendus && formation.descriptifs.attendus !== "") ||
      formation.critèresAnalyse.length > 0 ||
      formation.admis.répartition.parBac.length > 0 ||
      formation.admis.moyenneGénérale.centilles.length > 0
    ) {
      onglets.push({
        titre: i18n.PAGE_FORMATION.ONGLET_CRITÈRES,
        contenu: (
          <OngletCritèresFicheFormation
            critèresAnalyse={formation.critèresAnalyse}
            descriptifAttendus={formation.descriptifs.attendus}
            moyenneGénérale={formation.admis.moyenneGénérale}
            répartitionParBac={formation.admis.répartition.parBac}
          />
        ),
      });
    }

    if (formation.descriptifs.conseils && formation.descriptifs.conseils !== "") {
      onglets.push({
        titre: i18n.PAGE_FORMATION.ONGLET_CONSEILS,
        contenu: (
          <TexteTronqué
            nombreDeLigneÀAfficher="4"
            texte={formation.descriptifs.conseils}
          />
        ),
      });
    }

    return onglets;
  };

  return (
    <div className="bg-white pt-12">
      <div className="mb-6">
        <Badge
          titre={i18n.COMMUN.FORMATION}
          type="alerte"
        />
      </div>
      <div className="*:mb-6">
        <Titre niveauDeTitre="h1">{formation.nom}</Titre>
      </div>
      <div className="grid grid-flow-row gap-4 text-[--text-mention-grey]">
        {formation.admis.total && (
          <div className="grid grid-flow-col items-start justify-start gap-2">
            <span
              aria-hidden="true"
              className="fr-icon-team-fill fr-icon--sm"
            />
            <p className="mb-0 text-sm">
              <strong>{formation.admis.total} </strong> {i18n.PAGE_FORMATION.ÉLÈVES_ADMIS_ANNÉE_PRÉCÉDENTE}
            </p>
          </div>
        )}
        {formation.villes.length > 0 && (
          <div className="grid grid-flow-col items-start justify-start gap-2">
            <span
              aria-hidden="true"
              className="fr-icon-map-pin-2-fill fr-icon--sm"
            />
            <div>
              <p className="mb-2 text-sm">
                {i18n.PAGE_FORMATION.VILLES_PROPOSANT_LA_FORMATION} : <strong>{formation.villes.join(" • ")}</strong>
              </p>
              <LienExterne
                ariaLabel={i18n.PAGE_FORMATION.VOIR_SUR_PARCOURSUP}
                href="https://parcoursup.fr/"
                taille="petit"
                variante="simple"
              >
                {i18n.PAGE_FORMATION.VOIR_SUR_PARCOURSUP}
              </LienExterne>
            </div>
          </div>
        )}
      </div>
      <div className="mt-9">
        <BoutonsFavorisCorbeille taille="grand" />
      </div>
      <hr className="mb-9 mt-5" />
      <div className="mb-12 grid">
        <Onglets
          nomAccessible={i18n.ACCESSIBILITÉ.ONGLETS_FORMATION}
          onglets={générerLesOnglets()}
        />
      </div>
      <MétiersAccessiblesFicheFormation métiers={formation.métiersAccessibles} />
    </div>
  );
};

export default FicheFormation;
