import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const rechercheCommunesQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["communes", "recherche"],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const communes = await dépendances.rechercherCommunesUseCase.run(recherche);

      return communes ?? [];
    },
    enabled: false,
  });
