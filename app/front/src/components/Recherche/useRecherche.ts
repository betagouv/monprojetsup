import { UseRechercheArgs } from "./Recherche.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { StatusFormulaire } from "@/types/commons";
import { useEffect, useState } from "react";
import { useDebounceCallback } from "usehooks-ts";

export default function useRecherche({
  nombreDeCaractèresMinimumRecherche,
  nombreDeCaractèresMaximumRecherche,
  rechercheCallback,
  nombreDeRésultats,
}: UseRechercheArgs) {
  const [statusChampDeRecherche, setStatusChampDeRecherche] = useState<StatusFormulaire | undefined>();

  const lancerRecherche = async (recherche?: string) => {
    setStatusChampDeRecherche(undefined);

    if (!recherche) {
      await rechercheCallback(undefined);
      return;
    }

    if (recherche.length < nombreDeCaractèresMinimumRecherche) {
      await rechercheCallback(undefined);
      setStatusChampDeRecherche({
        type: "erreur" as const,
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_X_CARACTÈRES} ${nombreDeCaractèresMinimumRecherche} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });

      return;
    }

    if (recherche.length > nombreDeCaractèresMaximumRecherche) {
      await rechercheCallback(undefined);
      setStatusChampDeRecherche({
        type: "erreur" as const,
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.MOINS_DE_X_CARACTÈRES} ${nombreDeCaractèresMaximumRecherche} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });

      return;
    }

    await rechercheCallback(recherche);
  };

  useEffect(() => {
    if (nombreDeRésultats !== undefined && nombreDeRésultats === 0) {
      setStatusChampDeRecherche({ type: "erreur" as const, message: i18n.COMMUN.ERREURS_FORMULAIRES.AUCUN_RÉSULTAT });
    }
  }, [nombreDeRésultats]);

  const debouncedSetRecherche = useDebounceCallback(lancerRecherche, 400);

  return {
    statusChampDeRecherche,
    debouncedSetRecherche,
    nombreDeRésultats,
  };
}
