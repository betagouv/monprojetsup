import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { type ChampDeRechercheFormulaireProps } from "@/components/ChampDeRechercheFormulaire/ChampDeRechercheFormulaire.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { useRouter } from "@tanstack/react-router";
import { useState } from "react";

export default function useRechercheFormations() {
  const NB_CARACTÈRES_MINIMUM_RECHERCHE = 2;
  const router = useRouter();
  const { réinitialiserÉlémentAffiché } = actionsListeEtAperçuStore();

  const [status, setStatus] = useState<ChampDeRechercheFormulaireProps["status"]>();

  const rechercher = async (recherche: string) => {
    if (recherche && recherche.length >= NB_CARACTÈRES_MINIMUM_RECHERCHE) {
      réinitialiserÉlémentAffiché();
      await router.navigate({ to: "/formations", search: { recherche } });
    } else if (recherche && recherche.length < NB_CARACTÈRES_MINIMUM_RECHERCHE) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${NB_CARACTÈRES_MINIMUM_RECHERCHE} ${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES_SUITE}`,
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
