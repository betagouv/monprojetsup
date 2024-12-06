import useRechercheFormations from "./useRechercheFormations";
import { rechercheListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import ChampDeRechercheFormulaire from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire";
import { i18n } from "@/configuration/i18n/i18n";

const RechercheFormations = () => {
  const recherche = rechercheListeEtAperçuStore();
  const { statusChampDeRecherche, rechercher } = useRechercheFormations();

  return (
    <ChampDeRechercheFormulaire
      àLaSoumission={rechercher}
      entête={{ labelAccessibilité: i18n.PAGE_FORMATION.CHAMP_RECHERCHE_LABEL }}
      key={recherche}
      obligatoire={false}
      placeholder={i18n.PAGE_FORMATION.CHAMP_RECHERCHE_PLACEHOLDER}
      status={statusChampDeRecherche}
      valeurParDéfaut={recherche}
    />
  );
};

export default RechercheFormations;
