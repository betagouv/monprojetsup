import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { type ChampDeRechercheFormulaireProps } from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { useState } from "react";

export default function useRechercheFormations() {
  const { rechercher, réinitialiserRecherche } = actionsListeEtAperçuStore();

  const [status, setStatus] = useState<ChampDeRechercheFormulaireProps["status"]>();

  const àLaRecherche = (recherche: string) => {
    if (recherche && recherche.length < constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
    } else if (recherche && recherche.length > constantes.FORMATIONS.NB_CARACTÈRES_MAX_RECHERCHE) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.MOINS_DE_X_CARACTÈRES} ${constantes.FORMATIONS.NB_CARACTÈRES_MAX_RECHERCHE} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
    } else if (recherche) {
      setStatus(undefined);
      rechercher(recherche);
      document.querySelector(`#${constantes.ACCESSIBILITÉ.LISTE_CARTES_ID}`)?.scrollTo({ top: 0 });
    } else {
      réinitialiserRecherche();
    }
  };

  return {
    statusChampDeRecherche: status,
    rechercher: àLaRecherche,
  };
}
