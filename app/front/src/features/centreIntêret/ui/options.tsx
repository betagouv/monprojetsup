import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const catégoriesCentresIntêretsQueryOptions = queryOptions({
  queryKey: ["catégoriesCentresIntêrets"],
  queryFn: async () => {
    const catégoriesCentresIntêrets = await dépendances.récupérerCentresIntêretsGroupésParCatégorieUseCase.run();

    return catégoriesCentresIntêrets ?? [];
  },
});
