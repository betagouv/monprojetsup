import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useSpécialité from "@/features/élève/ui/formulaires/ScolaritéForm/Spécialités/useSpécialité";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useMaSélectionSpécialités() {
  const { élève } = useÉlève();
  const { spécialitéVersFavori } = useSpécialité();
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const spécialitésUniques = useMemo(
    () =>
      Array.from(
        new Map(
          référentielDonnées?.bacs.flatMap((bac) => bac.spécialités).map((spécialité) => [spécialité.id, spécialité]),
        ).values(),
      ),
    [référentielDonnées?.bacs],
  );

  const spécialitésSélectionnées = useMemo(() => {
    const spécialitésDeÉlève = spécialitésUniques.filter((spécialité) => élève?.spécialités?.includes(spécialité.id));

    return spécialitésDeÉlève.map(spécialitéVersFavori) ?? [];
  }, [élève?.spécialités]);

  return {
    spécialitésSélectionnées,
  };
}
