import OngletCritèresFicheFormation from "./OngletCritèresFicheFormation/OngletCritèresFicheFormation";
import OngletFormationFicheFormation from "./OngletFormationFicheFormation/OngletFormationFicheFormation";
import { type OngletsFicheFormationProps } from "./OngletsFicheFormation.interface";
import TexteTronqué from "@/components/TexteTronqué/TexteTronqué";
import { i18n } from "@/configuration/i18n/i18n";
import { Tabs } from "@codegouvfr/react-dsfr/Tabs";

const OngletsFicheFormation = ({ formation }: OngletsFicheFormationProps) => {
  const générerLesOnglets = () => {
    const onglets = [];

    if ((formation.descriptifs.formation && formation.descriptifs.formation !== "") || formation.liens.length > 0) {
      onglets.push({
        label: i18n.PAGE_FORMATION.ONGLET_FORMATION,
        content: (
          <OngletFormationFicheFormation
            liens={formation.liens}
            texte={formation.descriptifs.formation}
          />
        ),
      });
    }

    if (formation.descriptifs.détails && formation.descriptifs.détails !== "") {
      onglets.push({
        label: i18n.PAGE_FORMATION.ONGLET_DÉTAILS,
        content: <TexteTronqué texte={formation.descriptifs.détails} />,
      });
    }

    if (
      (formation.descriptifs.attendus && formation.descriptifs.attendus !== "") ||
      formation.critèresAnalyse.length > 0 ||
      formation.admis.répartition.parBac.length > 0 ||
      formation.admis.moyenneGénérale.centiles.length > 0
    ) {
      onglets.push({
        label: i18n.PAGE_FORMATION.ONGLET_CRITÈRES,
        content: (
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
        label: i18n.PAGE_FORMATION.ONGLET_CONSEILS,
        content: <TexteTronqué texte={formation.descriptifs.conseils} />,
      });
    }

    return onglets;
  };

  return (
    <Tabs
      label={i18n.ACCESSIBILITÉ.ONGLETS_FORMATION}
      tabs={générerLesOnglets()}
    />
  );
};

export default OngletsFicheFormation;
