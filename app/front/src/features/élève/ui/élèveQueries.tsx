import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { queryOptions } from "@tanstack/react-query";

export const élèveQueryOptions = queryOptions({
  queryKey: ["élève"],
  queryFn: async () => {
    return (await dépendances.récupérerProfilÉlèveUseCase.run()) ?? null;
  },
});

queryClient.setMutationDefaults(["mettreÀJourÉlève"], {
  mutationFn: async (élève: Élève) => {
    return await dépendances.mettreÀJourProfilÉlèveUseCase.run(élève);
  },
  onSuccess: async () => {
    await queryClient.invalidateQueries(élèveQueryOptions);
  },
});
