import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const queryÉlèveKeys = {
  PROFIL: "élève",
};

export const élèveQueryOptions = queryOptions({
  queryKey: [queryÉlèveKeys.PROFIL],
  queryFn: async () => {
    const réponse = await dépendances.récupérerProfilÉlèveUseCase.run();

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
});
