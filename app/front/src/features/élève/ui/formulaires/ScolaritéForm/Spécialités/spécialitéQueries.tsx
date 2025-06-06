import { dépendances } from "@/configuration/dépendances/dépendances";
import { BacÉlève, Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { queryOptions } from "@tanstack/react-query";

export const rechercheSpécialitésQueryOptions = (bac: BacÉlève, spécialités: Spécialité[], recherche?: string) =>
  queryOptions({
    queryKey: ["spécialités", bac, "recherche", recherche],
    queryFn: () => {
      if (recherche === undefined) return null;

      return dépendances.rechercherSpécialitésUseCase.run(recherche, spécialités);
    },
  });
