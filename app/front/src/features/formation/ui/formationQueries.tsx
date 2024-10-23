import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Formation } from "@/features/formation/domain/formation.interface";
import { queryOptions } from "@tanstack/react-query";

export const récupérerFormationQueryOptions = (formationId: Formation["id"] | null) =>
  queryOptions({
    queryKey: ["formations", formationId],
    queryFn: async () => {
      if (formationId === null) {
        return null;
      }

      return (await dépendances.récupérerFormationUseCase.run(formationId)) ?? null;
    },
  });

export const récupérerFormationsQueryOptions = (formationIds: Array<Formation["id"]>) =>
  queryOptions({
    queryKey: ["formations", formationIds],
    queryFn: async () => {
      if (formationIds.length === 0) return [];

      const formations = await dépendances.récupérerFormationsUseCase.run(formationIds);

      return formations ?? [];
    },
  });

export const rechercherFormationsQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["formations", "rechercher", recherche],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const formations = await dépendances.rechercherFormationsUseCase.run(recherche);

      return formations ?? [];
    },
    enabled: false,
  });

export const suggérerFormationsQueryOptions = queryOptions({
  queryKey: ["formationsSuggestions"],
  queryFn: async () => {
    const formations = await dépendances.suggérerFormationsUseCase.run();

    return formations ?? [];
  },
});
