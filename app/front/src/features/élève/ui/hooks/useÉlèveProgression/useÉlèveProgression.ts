import { ProgressionÉlève } from "@/features/élève/domain/élève.interface";
import { progressionQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useÉlèveProgression() {
  const { data: progression } = useQuery(progressionQueryOptions);

  const récupérerProgression = useMemo((): ProgressionÉlève => progression ?? 0, [progression]);

  return {
    récupérerProgression,
  };
}
