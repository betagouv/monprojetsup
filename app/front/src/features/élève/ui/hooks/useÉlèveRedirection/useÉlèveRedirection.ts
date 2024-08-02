/* eslint-disable react-hooks/rules-of-hooks */
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { étapesÉtapesInscriptionÉlèveStore } from "@/features/élève/ui/store/useÉtapesInscriptionÉlève/useÉtapesInscriptionÉlève";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import { useQuery } from "@tanstack/react-query";
import { useRouter, useRouterState } from "@tanstack/react-router";
import { useLayoutEffect, useState } from "react";

export default function useÉlèveRedirection() {
  const [estInitialisé, setEstInitialisé] = useState(false);

  const router = useRouter();
  const routerState = useRouterState();
  const utilisateur = useUtilisateur();
  const { data: élève } = useQuery(élèveQueryOptions);
  const étapesInscription = étapesÉtapesInscriptionÉlèveStore();

  useLayoutEffect(() => {
    if (utilisateur.type === undefined || (utilisateur.type === "élève" && !élève)) return;

    if (utilisateur.type !== "élève") {
      setEstInitialisé(true);
      return;
    }

    const critèresRemplissagePourÉtapesInscription = [
      Boolean(élève?.situation),
      Boolean(élève?.classe),
      élève?.domaines && élève.domaines.length > 0,
      élève?.centresIntêrets && élève.centresIntêrets.length > 0,
      Boolean(élève?.métiersFavoris),
      Boolean(élève?.communesFavorites),
      Boolean(élève?.formationsFavorites),
    ];

    const indexÉtapeNonRemplie = critèresRemplissagePourÉtapesInscription.findIndex((étape) => !étape);

    if (indexÉtapeNonRemplie !== -1) {
      const routesAutorisées = étapesInscription.slice(0, indexÉtapeNonRemplie + 1).map((étape) => étape.url);
      const urlDeRedirection = étapesInscription[indexÉtapeNonRemplie].url;

      if (!routesAutorisées.includes(routerState.location.pathname as keyof (typeof router)["routesByPath"])) {
        router.navigate({ to: urlDeRedirection });
      }
    }

    setEstInitialisé(true);
  }, [router, routerState.location.pathname, utilisateur.type, élève, étapesInscription]);

  return { estInitialisé };
}
