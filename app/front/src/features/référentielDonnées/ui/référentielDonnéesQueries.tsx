import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const référentielDonnéesQueryOptions = queryOptions({
  queryKey: ["référentielDonnées"],
  queryFn: async () => {
    const réponse = await dépendances.récupérerRéférentielDonnéesUseCase.run();

    if (réponse instanceof Error) {
      throw réponse;
    }

    return réponse;
  },
});
