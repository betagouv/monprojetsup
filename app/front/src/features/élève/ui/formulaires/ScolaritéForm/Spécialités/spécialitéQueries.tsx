import { dépendances } from "@/configuration/dépendances/dépendances";
import { Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { queryOptions } from "@tanstack/react-query";

export const rechercheSpécialitésQueryOptions = (spécialités: Spécialité[], recherche?: string) =>
  queryOptions({
    queryKey: ["spécialités", "recherche", recherche],
    queryFn: () => {
      if (recherche === undefined) return null;

      return dépendances.rechercherSpécialitésUseCase.run(recherche, spécialités);
    },
  });
