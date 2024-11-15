import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { type ChampDeRechercheFormulaireProps } from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { useState } from "react";

export default function useRechercheFormations() {
  const { rechercher, réinitialiserRecherche } = actionsListeEtAperçuStore();

  const [status, setStatus] = useState<ChampDeRechercheFormulaireProps["status"]>();

  const àLaRecherche = (recherche: string) => {
    if (recherche && recherche.length >= constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE) {
      setStatus(undefined);
      rechercher(recherche);
      document.querySelector("#liste-formations")?.scrollTo({ top: 0 });
    } else if (recherche && recherche.length < constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
    } else {
      réinitialiserRecherche();
    }
  };

  return {
    statusChampDeRecherche: status,
    rechercher: àLaRecherche,
  };
}
