import { Commune } from "@/features/commune/domain/commune.interface";
import useCommune from "@/features/commune/ui/useCommune";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo } from "react";

export default function useMaSélectionCommunes() {
  const { élève } = useÉlève({});
  const { communeVersFavori } = useCommune();

  const favoris = useMemo(() => {
    const communesFavoritesVersCommunes: Commune[] =
      élève?.communesFavorites?.map((commune) => ({
        ...commune,
        codePostal: "",
      })) ?? [];

    return communesFavoritesVersCommunes.map(communeVersFavori);
  }, [élève?.communesFavorites]);

  return {
    favoris,
  };
}
