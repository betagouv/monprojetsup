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

export const spécialitésPourUnBacQueryOptions = (bacId: Bac["id"]) =>
  queryOptions({
    queryKey: ["spécialités", bacId],
    queryFn: async () => {
      const spécialités = await dépendances.récupérerSpécialitésPourUnBacUseCase.run(bacId);

      return spécialités ?? [];
    },
  });
