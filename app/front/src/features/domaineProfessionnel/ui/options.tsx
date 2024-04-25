import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const catégoriesDomainesProfessionnelsQueryOptions = queryOptions({
  queryKey: ["catégoriesDomainesProfessionnels"],
  queryFn: async () => {
    const catégoriesDomainesProfessionnels =
      await dépendances.récupérerDomainesProfessionnelsGroupésParCatégorieUseCase.run();

    return catégoriesDomainesProfessionnels ?? [];
  },
});
