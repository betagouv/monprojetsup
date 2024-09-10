import { i18n } from "@/configuration/i18n/i18n";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { rechercherFormationsQueryOptions } from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo, useState } from "react";
import { useDebounceCallback } from "usehooks-ts";

export default function useRechercheFormations() {
  const NB_CARACTÈRES_MINIMUM_RECHERCHE = 3;

  const [recherche, setRecherche] = useState<string>();
  const debouncedSetRecherche = useDebounceCallback(setRecherche, 400);

  const {
    data: formations,
    refetch: rechercher,
    isFetching: rechercheEnCours,
  } = useQuery(rechercherFormationsQueryOptions(recherche));

  const statusChampDeRecherche = useMemo(() => {
    if (recherche && recherche.length < NB_CARACTÈRES_MINIMUM_RECHERCHE) {
      return {
        type: "erreur" as const,
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${NB_CARACTÈRES_MINIMUM_RECHERCHE} ${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES_SUITE}`,
      };
    }

    if (recherche && formations?.length === 0 && !rechercheEnCours)
      return { type: "erreur" as const, message: i18n.COMMUN.ERREURS_FORMULAIRES.AUCUN_RÉSULTAT };

    return undefined;
  }, [NB_CARACTÈRES_MINIMUM_RECHERCHE, formations, recherche, rechercheEnCours]);

  useEffect(() => {
    if (recherche && recherche.length >= NB_CARACTÈRES_MINIMUM_RECHERCHE) {
      rechercher();
    } else {
      queryClient.setQueryData(["formations", "rechercher"], () => null);
    }
  }, [recherche, rechercher]);

  return {
    statusChampDeRecherche,
    debouncedSetRecherche,
  };
}
