import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Formation } from "@/features/formation/domain/formation.interface";
import { queryOptions } from "@tanstack/react-query";

export const rechercheFormationsQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["formations", "recherche"],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const formations = await dépendances.rechercherFormationsUseCase.run(recherche);

      return formations ?? [];
    },
    enabled: false,
  });

export const récupérerAperçusFormationsQueryOptions = (formationIds?: Array<Formation["id"]>) =>
  queryOptions({
    queryKey: ["formations", "aperçu", formationIds],
    queryFn: async () => {
      if (formationIds === undefined) return [];

      const formations = await dépendances.récupérerAperçusFormationsUseCase.run(formationIds);

      return formations ?? [];
    },
    enabled: true,
  });

export const détailFormationQueryOptions = (formationId: Formation["id"]) =>
  queryOptions({
    queryKey: ["formation", formationId],
    queryFn: async () => {
      return (await dépendances.récupérerDétailFormationUseCase.run(formationId)) ?? null;
    },
  });
