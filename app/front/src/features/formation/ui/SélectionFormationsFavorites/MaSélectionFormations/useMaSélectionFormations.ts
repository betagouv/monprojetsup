import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { récupérerFormationsQueryOptions } from "@/features/formation/ui/formationQueries.tsx";
import useFormation from "@/features/formation/ui/useFormation";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useMaSélectionFormations() {
  const { élève } = useÉlève();
  const { formationVersFavori } = useFormation();
  const { data: formationsSélectionnées } = useQuery(récupérerFormationsQueryOptions(élève?.formations ?? []));

  const favoris = useMemo(() => {
    return formationsSélectionnées?.map(formationVersFavori) ?? [];
  }, [formationsSélectionnées]);

  return {
    favoris,
  };
}
