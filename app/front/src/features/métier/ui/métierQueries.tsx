import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { type Métier } from "@/features/métier/domain/métier.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";
import { queryOptions } from "@tanstack/react-query";

export const récupérerMétierQueryOptions = (métierId: Métier["id"] | null) =>
  queryOptions({
    queryKey: ["métiers", métierId],
    queryFn: async () => {
      if (métierId === null) return null;

      const réponse = await dépendances.récupérerMétierUseCase.run(métierId);

      if (réponse instanceof RessourceNonTrouvéeErreur) {
        return null;
      }

      if (réponse instanceof Error) {
        throw réponse;
      }

      return réponse ?? null;
    },
  });

export const récupérerMétiersQueryOptions = (métierIds: Array<Métier["id"]>) =>
  queryOptions({
    queryKey: ["métiers", métierIds],
    queryFn: async () => {
      if (métierIds.length === 0) return [];

      const réponse = await dépendances.récupérerMétiersUseCase.run(métierIds);

      if (réponse instanceof Error) {
        throw réponse;
      }

      return réponse;
    },
  });

export const rechercherMétiersQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["métiers", "rechercher", recherche],
    queryFn: async () => {
      if (recherche === undefined) return null;

      const réponse = await dépendances.rechercherMétiersUseCase.run(recherche);

      if (réponse instanceof Error) {
        throw réponse;
      }

      for (const métier of réponse) {
        queryClient.setQueryData(["métiers", métier.id], métier);
      }

      return réponse;
    },
  });
