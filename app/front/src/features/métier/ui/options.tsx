import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Métier } from "@/features/métier/domain/métier.interface";
import { queryOptions } from "@tanstack/react-query";

export const rechercheMétiersQueryOptions = (recherche?: string) =>
  queryOptions({
    queryKey: ["métiers", "recherche"],
    queryFn: async () => {
      if (recherche === undefined) return [];

      const métiers = await dépendances.rechercherMétiersUseCase.run(recherche);

      return métiers ?? [];
    },
    enabled: false,
  });

export const récupérerAperçusMétiersQueryOptions = (métierIds?: Array<Métier["id"]>) =>
  queryOptions({
    queryKey: ["métiers", "aperçu", métierIds],
    queryFn: async () => {
      if (métierIds === undefined) return [];

      const métiers = await dépendances.récupérerAperçusMétiersUseCase.run(métierIds);

      return métiers ?? [];
    },
    enabled: true,
  });
