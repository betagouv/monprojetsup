import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { étapesInscriptionÉlèveStore } from "@/features/élève/ui/ParcoursInscriptionÉlève/useInscriptionÉlèveStore/useInscriptionÉlèveStore";
import useUtilisateur from "@/features/utilisateur/ui/useUtilisateur";
import { Paths } from "@/types/commons";
import { useQuery } from "@tanstack/react-query";
import { useRouter, useRouterState } from "@tanstack/react-router";
import { useLayoutEffect, useState } from "react";

export default function useÉlèveRedirection() {
  const [estInitialisé, setEstInitialisé] = useState(false);

  const router = useRouter();
  const routerState = useRouterState();
  const utilisateur = useUtilisateur();
  const { data: élève } = useQuery(élèveQueryOptions);
  const { élèveAuMoinsUnDomaineFavori, élèveAuMoinsUnCentreIntêretFavori } = useÉlève();
  const étapesInscription = étapesInscriptionÉlèveStore();

  useLayoutEffect(() => {
    if (utilisateur.id === undefined || !élève) return;

    const critèresRemplissagePourÉtapesInscription = [
      Boolean(élève?.situation),
      Boolean(élève?.classe),
      élèveAuMoinsUnDomaineFavori,
      élèveAuMoinsUnCentreIntêretFavori,
      Boolean(élève?.métiersFavoris),
      Boolean(élève?.communesFavorites),
      Boolean(élève?.formations),
    ];

    const indexÉtapeNonRemplie = critèresRemplissagePourÉtapesInscription.findIndex((étape) => !étape);

    if (indexÉtapeNonRemplie !== -1) {
      const routesAutorisées = étapesInscription.slice(0, indexÉtapeNonRemplie + 1).map((étape) => étape.url);
      const urlDeRedirection = étapesInscription[indexÉtapeNonRemplie].url;

      if (!routesAutorisées.includes(routerState.location.pathname as Paths)) {
        void router.navigate({ to: urlDeRedirection });
      }
    }

    setEstInitialisé(true);
  }, [router, routerState.location.pathname, utilisateur, élève, étapesInscription]);

  return { estInitialisé };
}
