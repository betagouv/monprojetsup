import useRechercheFormations from "./useRechercheFormations";
import ChampDeRecherche from "@/components/_dsfr/ChampDeRecherche/ChampDeRecherche";
import { i18n } from "@/configuration/i18n/i18n";

const RechercheFormations = () => {
  const { statusChampDeRecherche, debouncedSetRecherche } = useRechercheFormations();

  return (
    <ChampDeRecherche
      auChangement={(événement) => debouncedSetRecherche(événement.target.value ?? undefined)}
      entête={{ labelAccessibilité: i18n.PAGE_FORMATION.CHAMP_RECHERCHE_LABEL }}
      obligatoire={false}
      placeholder={i18n.PAGE_FORMATION.CHAMP_RECHERCHE_PLACEHOLDER}
      status={statusChampDeRecherche}
    />
  );
};

export default RechercheFormations;
