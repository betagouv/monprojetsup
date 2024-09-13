import useRechercheFormations from "./useRechercheFormations";
import ChampDeRechercheFormulaire from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire";
import { i18n } from "@/configuration/i18n/i18n";

const RechercheFormations = () => {
  const { statusChampDeRecherche, rechercher } = useRechercheFormations();

  return (
    <ChampDeRechercheFormulaire
      entête={{ labelAccessibilité: i18n.PAGE_FORMATION.CHAMP_RECHERCHE_LABEL }}
      obligatoire={false}
      placeholder={i18n.PAGE_FORMATION.CHAMP_RECHERCHE_PLACEHOLDER}
      status={statusChampDeRecherche}
      àLaSoumission={rechercher}
    />
  );
};

export default RechercheFormations;
