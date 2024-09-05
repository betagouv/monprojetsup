import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const référentielDonnéesQueryOptions = queryOptions({
  queryKey: ["référentielDonnées"],
  queryFn: async () => {
    return (await dépendances.récupérerRéférentielDonnéesUseCase.run()) ?? null;
  },
});
