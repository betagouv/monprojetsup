import { type RechercheFormationsProps } from "./RechercheFormations.interface";
import useRechercheFormations from "./useRechercheFormations";
import ChampDeRechercheFormulaire from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire";
import { i18n } from "@/configuration/i18n/i18n";

const RechercheFormations = ({ valeurParDéfaut }: RechercheFormationsProps) => {
  const { statusChampDeRecherche, rechercher } = useRechercheFormations();

  return (
    <ChampDeRechercheFormulaire
      entête={{ labelAccessibilité: i18n.PAGE_FORMATION.CHAMP_RECHERCHE_LABEL }}
      obligatoire={false}
      placeholder={i18n.PAGE_FORMATION.CHAMP_RECHERCHE_PLACEHOLDER}
      status={statusChampDeRecherche}
      valeurParDéfaut={valeurParDéfaut}
      àLaSoumission={rechercher}
    />
  );
};

export default RechercheFormations;
