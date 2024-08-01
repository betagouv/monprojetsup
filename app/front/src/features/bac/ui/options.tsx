import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Spécialité } from "@/features/bac/domain/bac.interface";
import { queryOptions } from "@tanstack/react-query";

export const bacsQueryOptions = queryOptions({
  queryKey: ["bacs"],
  queryFn: async () => {
    const bacs = await dépendances.récupérerBacsUseCase.run();

    return bacs ?? [];
  },
});

export const rechercheSpécialitésQueryOptions = (bacId: string | null, recherche?: string) =>
  queryOptions({
    queryKey: ["spécialités", "recherche", bacId, recherche],
    queryFn: async () => {
      if (!bacId) return [];

      const spécialités = await dépendances.rechercherSpécialitésPourUnBacUseCase.run(bacId, recherche);

      return spécialités ?? [];
    },
    enabled: false,
  });

export const récupérerSpécialitésQueryOptions = (spécialitéIds: Array<Spécialité["id"]> | null) =>
  queryOptions({
    queryKey: ["spécialités", spécialitéIds],
    queryFn: async () => {
      if (!spécialitéIds) return [];

      const spécialités = await dépendances.récupérerSpécialitésUseCase.run(spécialitéIds);

      return spécialités ?? [];
    },
    enabled: true,
  });
