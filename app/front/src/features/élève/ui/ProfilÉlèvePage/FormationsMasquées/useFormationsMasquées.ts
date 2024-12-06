import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { récupérerFichesFormationsQueryOptions } from "@/features/formation/ui/formationQueries.tsx";
import { useQuery } from "@tanstack/react-query";

export default function useFormationsMasquées() {
  const { élève } = useÉlève();
  const { data: formationsMasquées } = useQuery(récupérerFichesFormationsQueryOptions(élève?.formationsMasquées ?? []));

  return { formationsMasquées };
}
