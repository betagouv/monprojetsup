import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const rechercheVillesQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["villes", "recherche"],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const villes = await dépendances.rechercherVillesUseCase.run(recherche);

      return villes ?? [];
    },
    enabled: false,
  });
