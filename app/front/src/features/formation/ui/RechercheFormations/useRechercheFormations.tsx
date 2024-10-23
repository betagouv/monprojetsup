import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { type ChampDeRechercheFormulaireProps } from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { useRouter } from "@tanstack/react-router";
import { useState } from "react";

export default function useRechercheFormations() {
  const router = useRouter();
  const { réinitialiserÉlémentAffiché } = actionsListeEtAperçuStore();

  const [status, setStatus] = useState<ChampDeRechercheFormulaireProps["status"]>();

  const rechercher = async (recherche: string) => {
    if (recherche && recherche.length >= constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE) {
      réinitialiserÉlémentAffiché();
      await router.navigate({ to: "/formations", search: { recherche } });
    } else if (recherche && recherche.length < constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${constantes.FORMATIONS.NB_CARACTÈRES_MIN_RECHERCHE} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
    } else {
      réinitialiserÉlémentAffiché();
      queryClient.setQueryData(["formations", "rechercher"], () => null);
      await router.navigate({ to: "/formations" });
    }
  };

  return {
    statusChampDeRecherche: status,
    rechercher,
  };
}
