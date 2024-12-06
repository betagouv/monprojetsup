import { récupérerMétiersQueryOptions } from "@/features/métier/ui/métierQueries.tsx";
import useMétier from "@/features/métier/ui/SélectionMétiersFavoris/useMétier.tsx";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useMaSélectionMétiers() {
  const { élève } = useÉlève();
  const { métierVersFavori } = useMétier();
  const { data: métiersFavoris } = useQuery(récupérerMétiersQueryOptions(élève?.métiersFavoris ?? []));

  const favoris = useMemo(() => {
    return métiersFavoris?.map(métierVersFavori) ?? [];
  }, [métiersFavoris]);

  return {
    favoris,
  };
}
