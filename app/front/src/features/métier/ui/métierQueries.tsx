import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Métier } from "@/features/métier/domain/métier.interface";
import { queryOptions } from "@tanstack/react-query";

export const récupérerMétierQueryOptions = (métierId: Métier["id"] | null) =>
  queryOptions({
    queryKey: ["métiers", métierId],
    queryFn: async () => {
      if (métierId === null) {
        return null;
      }

      return (await dépendances.récupérerMétierUseCase.run(métierId)) ?? null;
    },
  });

export const récupérerMétiersQueryOptions = (métierIds: Array<Métier["id"]>) =>
  queryOptions({
    queryKey: ["métiers", métierIds],
    queryFn: async () => {
      if (métierIds.length === 0) return [];

      const métiers = await dépendances.récupérerMétiersUseCase.run(métierIds);

      return métiers ?? [];
    },
    enabled: true,
  });

export const rechercherMétiersQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["métiers", "rechercher", recherche],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const métiers = await dépendances.rechercherMétiersUseCase.run(recherche);

      return métiers ?? [];
    },
    enabled: false,
  });
