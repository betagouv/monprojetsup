import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const élèveQueryOptions = queryOptions({
  queryKey: ["élève"],
  queryFn: async () => {
    return await dépendances.récupérerProfilÉlèveUseCase.run();
  },
});
