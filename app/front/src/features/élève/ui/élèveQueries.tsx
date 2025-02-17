import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Élève, ProgressionÉlève } from "@/features/élève/domain/élève.interface";
import { queryOptions } from "@tanstack/react-query";

export const queryÉlèveKeys = {
  PROFIL: "élève",
  PROGRESSION: "progression",
};

export const élèveQueryOptions = queryOptions({
  queryKey: [queryÉlèveKeys.PROFIL],
  queryFn: async (): Promise<Élève> => {
    const élève = await dépendances.récupérerProfilÉlèveUseCase.run();
    if (élève instanceof Error) throw élève;
    return élève;
  },
});

export const progressionQueryOptions = queryOptions({
  queryKey: [queryÉlèveKeys.PROGRESSION],
  queryFn: async (): Promise<ProgressionÉlève> => {
    return await dépendances.récupérerProgressionÉlèveUseCase.run();
  },
});
