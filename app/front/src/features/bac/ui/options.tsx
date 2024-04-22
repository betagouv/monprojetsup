import { dépendances } from "@/configuration/dépendances/dépendances";
import { type Bac } from "@/features/bac/domain/bac.interface";
import { queryOptions } from "@tanstack/react-query";

export const bacsQueryOptions = queryOptions({
  queryKey: ["bacs"],
  queryFn: async () => {
    const bacs = await dépendances.récupérerBacsUseCase.run();

    return bacs ?? [];
  },
});

export const spécialitésPourUnBacQueryOptions = (bacId?: Bac["id"]) =>
  queryOptions({
    queryKey: ["spécialités", bacId],
    queryFn: async ({ queryKey }) => {
      if (queryKey[1] === undefined) return [];

      const spécialités = await dépendances.récupérerSpécialitésPourUnBacUseCase.run(queryKey[1]);

      return spécialités ?? [];
    },
    enabled: false,
  });
