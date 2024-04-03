/* eslint-disable react-hooks/rules-of-hooks */
import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { useMutation } from "@tanstack/react-query";

export default function useÉlève() {
  const mutationÉlève = useMutation({
    mutationFn: async (data: Partial<Omit<Élève, "id">>) => {
      return await dépendances.mettreÀJourÉlèveUseCase.run(data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["élève"] });
    },
  });

  return {
    mettreÀJourÉlève: mutationÉlève.mutateAsync,
  };
}
