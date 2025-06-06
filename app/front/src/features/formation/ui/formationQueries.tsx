import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { type FicheFormation, Formation } from "@/features/formation/domain/formation.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";
import { queryOptions } from "@tanstack/react-query";

export const récupérerFicheFormationQueryOptions = (formationId: FicheFormation["id"] | null) =>
  queryOptions({
    queryKey: ["formations", "fiche", formationId],
    queryFn: async () => {
      if (formationId === null) {
        return null;
      }

      const réponse = await dépendances.récupérerFicheFormationUseCase.run(formationId);

      if (réponse instanceof RessourceNonTrouvéeErreur) {
        return null;
      }

      if (réponse instanceof Error) {
        throw réponse;
      }

      return réponse ?? null;
    },
  });

export const récupérerFichesFormationsQueryOptions = (formationIds: Array<FicheFormation["id"]>) =>
  queryOptions({
    queryKey: ["formations", "fiche", formationIds],
    queryFn: async () => {
      if (formationIds.length === 0) return [];

      const réponse = await dépendances.récupérerFichesFormationsUseCase.run(formationIds);

      if (réponse instanceof Error) {
        throw réponse;
      }

      for (const fiche of réponse) {
        queryClient.setQueryData(["formations", "fiche", fiche.id], fiche);
      }

      return réponse;
    },
  });

export const récupérerFormationsQueryOptions = (formationIds: Array<Formation["id"]>) =>
  queryOptions({
    queryKey: ["formations", formationIds],
    placeholderData: (previous) => previous ?? [],
    queryFn: async () => {
      if (formationIds.length === 0) return [];

      const donnéesExistantesEnCache = formationIds.map((formationId) =>
        queryClient.getQueryData<Formation>(["formations", formationId]),
      );

      if (donnéesExistantesEnCache.every((donnée) => donnée !== undefined)) {
        return donnéesExistantesEnCache;
      }

      const réponse = await dépendances.récupérerFormationsUseCase.run(formationIds);

      if (réponse instanceof Error) {
        throw réponse;
      }

      for (const formation of réponse) {
        queryClient.setQueryData(["formations", formation.id], formation);
      }

      return réponse;
    },
  });

export const rechercherFichesFormationsQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["formations", "fiche", "rechercher", recherche],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const réponse = await dépendances.rechercherFichesFormationsUseCase.run(recherche);

      if (réponse instanceof Error) {
        throw réponse;
      }

      for (const fiche of réponse) {
        queryClient.setQueryData(["formations", "fiche", fiche.id], fiche);
      }

      return réponse;
    },
    enabled: false,
  });

export const rechercherFormationsQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["formations", "rechercher", recherche],
    queryFn: async () => {
      if (recherche === undefined) return null;

      const réponse = await dépendances.rechercherFormationsUseCase.run(recherche);

      if (réponse instanceof Error) {
        throw réponse;
      }

      for (const formation of réponse) {
        queryClient.setQueryData(["formations", formation.id], formation);
      }

      return réponse;
    },
  });

export const suggérerFormationsQueryOptions = queryOptions({
  queryKey: ["formationsSuggestions"],
  queryFn: async () => {
    const réponse = await dépendances.suggérerFormationsUseCase.run();

    if (réponse instanceof Error) {
      throw réponse;
    }

    for (const fiche of réponse) {
      queryClient.setQueryData(["formations", "fiche", fiche.id], fiche);
    }

    return réponse;
  },
});
