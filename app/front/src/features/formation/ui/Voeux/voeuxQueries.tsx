import { dépendances } from "@/configuration/dépendances/dépendances";
import { Formation, Voeu } from "@/features/formation/domain/formation.interface";
import { queryOptions } from "@tanstack/react-query";

export const rechercheVoeuxQueryOptions = (idFormation: Formation["id"], voeux: Voeu[], recherche?: string) =>
  queryOptions({
    queryKey: ["voeux", idFormation, "recherche", recherche],
    queryFn: () => {
      if (recherche === undefined) return null;

      return dépendances.rechercherVoeuxUseCase.run(recherche, voeux);
    },
  });
