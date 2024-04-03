import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const élèveQueryOptions = queryOptions({
  queryKey: ["élève"],
  queryFn: async () => {
    const élève = await dépendances.récupérerÉlèveUseCase.run();

    if (!élève) {
      return await dépendances.créerÉlèveUseCase.run();
    }

    return élève;
  },
});
