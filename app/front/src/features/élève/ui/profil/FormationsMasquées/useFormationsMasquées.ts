import { récupérerFichesFormationsQueryOptions } from "@/features/formation/ui/formationQueries.tsx";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries.tsx";
import { useQuery } from "@tanstack/react-query";

export default function useFormationsMasquées() {
  const { data: élève } = useQuery(élèveQueryOptions);
  const { data: formationsMasquées } = useQuery(récupérerFichesFormationsQueryOptions(élève?.formationsMasquées ?? []));

  return { formationsMasquées };
}
