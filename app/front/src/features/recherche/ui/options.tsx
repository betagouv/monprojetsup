import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const rechercheQueryOptions = queryOptions({
  queryKey: ["recherche"],
  queryFn: async () => {
    const formations = await dépendances.récupérerFormationsUseCase.run();
    const métiers = await dépendances.récupérerMétiersUseCase.run();

    return {
      formations: formations ?? [],
      métiers: métiers ?? [],
    };
  },
});
