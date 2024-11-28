import { UseRechercheArgs } from "./Recherche.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { StatusFormulaire } from "@/types/commons";
import { useState } from "react";
import { useDebounceCallback } from "usehooks-ts";

export default function useRecherche<T>({
  nombreDeCaractèresMinimumRecherche,
  rechercheCallback,
}: UseRechercheArgs<T>) {
  const [statusChampDeRecherche, setStatusChampDeRecherche] = useState<StatusFormulaire | undefined>();
  const [nombreDeRésultats, setNombreDeRésultats] = useState<number | undefined>(undefined);

  const lancerRecherche = (recherche?: string): void => {
    setStatusChampDeRecherche(undefined);

    if (recherche && recherche.length < nombreDeCaractèresMinimumRecherche) {
      setStatusChampDeRecherche({
        type: "erreur" as const,
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${nombreDeCaractèresMinimumRecherche} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
    } else if (recherche) {
      const résultats = rechercheCallback(recherche);
      setNombreDeRésultats(résultats.length);

      if (résultats.length === 0) {
        setStatusChampDeRecherche({ type: "erreur" as const, message: i18n.COMMUN.ERREURS_FORMULAIRES.AUCUN_RÉSULTAT });
      }
    } else {
      setNombreDeRésultats(undefined);
    }
  };

  const debouncedSetRecherche = useDebounceCallback(lancerRecherche, 400);

  return {
    statusChampDeRecherche,
    debouncedSetRecherche,
    nombreDeRésultats,
  };
}
