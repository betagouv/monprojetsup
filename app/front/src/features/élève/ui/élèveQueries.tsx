import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryOptions } from "@tanstack/react-query";

export const queryÉlèveKeys = {
  PROFIL: "élève",
  PROGRESSION: "progression",
};

export const élèveQueryOptions = queryOptions({
  queryKey: [queryÉlèveKeys.PROFIL],
  queryFn: async () => {
    const réponse = await dépendances.récupérerProfilÉlèveUseCase.run();

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
});

export const progressionQueryOptions = queryOptions({
  queryKey: [queryÉlèveKeys.PROGRESSION],
  queryFn: async () => {
    const réponse = await dépendances.récupérerProgressionÉlèveUseCase.run();

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
});
