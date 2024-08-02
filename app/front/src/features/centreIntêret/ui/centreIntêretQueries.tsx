import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const centresIntêretsQueryOptions = queryOptions({
  queryKey: ["catégoriesCentreIntêret"],
  queryFn: async () => {
    const catégoriesCentreIntêret = await dépendances.récupérerCatégoriesEtSousCatégoriesCentreIntêretUseCase.run();

    return catégoriesCentreIntêret ?? [];
  },
});
