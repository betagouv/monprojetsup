import { récupérerFormationsQueryOptions } from "@/features/formation/ui/formationQueries.tsx";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries.tsx";
import { useQuery } from "@tanstack/react-query";

export default function useFormationsMasquéesForm() {
  const { data: élève } = useQuery(élèveQueryOptions);
  const { data: formationsMasquées } = useQuery(récupérerFormationsQueryOptions(élève?.formationsMasquées ?? []));

  return formationsMasquées;
}
