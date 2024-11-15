import useRechercheFormations from "./useRechercheFormations";
import { rechercheListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import ChampDeRechercheFormulaire from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire";
import { i18n } from "@/configuration/i18n/i18n";

const RechercheFormations = () => {
  const recherche = rechercheListeEtAperçuStore();
  const { statusChampDeRecherche, rechercher } = useRechercheFormations();

  return (
    <ChampDeRechercheFormulaire
      entête={{ labelAccessibilité: i18n.PAGE_FORMATION.CHAMP_RECHERCHE_LABEL }}
      key={recherche}
      obligatoire={false}
      placeholder={i18n.PAGE_FORMATION.CHAMP_RECHERCHE_PLACEHOLDER}
      status={statusChampDeRecherche}
      valeurParDéfaut={recherche}
      àLaSoumission={rechercher}
    />
  );
};

export default RechercheFormations;
