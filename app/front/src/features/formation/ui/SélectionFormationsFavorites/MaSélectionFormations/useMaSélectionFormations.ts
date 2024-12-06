import { récupérerFormationsQueryOptions } from "@/features/formation/ui/formationQueries.tsx";
import useFormation from "@/features/formation/ui/SélectionFormationsFavorites/useFormation.tsx";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useMaSélectionFormations() {
  const { élève } = useÉlève();
  const { formationVersFavori } = useFormation();
  const { data: formationsFavorites } = useQuery(
    récupérerFormationsQueryOptions(élève?.formationsFavorites?.map(({ id }) => id) ?? []),
  );

  const favoris = useMemo(() => {
    return formationsFavorites?.map(formationVersFavori) ?? [];
  }, [formationsFavorites]);

  return {
    favoris,
  };
}
