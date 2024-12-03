import { type UseÉlèveArgs } from "./useÉlève.interface";
import {
  CommuneFavorite,
  type FormationFavorite,
  VoeuFavori,
  type Élève,
} from "@/features/élève/domain/élève.interface";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function useÉlève({ àLaSoumissionDuFormulaireAvecSuccès }: UseÉlèveArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: ["mettreÀJourÉlève"] });

  const mettreÀJourÉlève = async (changements: Partial<Élève>) => {
    if (!élève) return;

    await mutationÉlève.mutateAsync({ ...élève, ...changements });
    àLaSoumissionDuFormulaireAvecSuccès?.();
  };

  const mettreÀJourUneFormationFavorite = async (
    formationId: FormationFavorite["id"],
    changements: Partial<FormationFavorite>,
  ) => {
    if (!élève) return;

    const nouvellesFormationsFavorites =
      élève.formationsFavorites?.map((formationFavorite) => {
        if (formationFavorite.id === formationId) {
          return { ...formationFavorite, ...changements };
        }

        return formationFavorite;
      }) ?? [];

    await mettreÀJourÉlève({
      formationsFavorites: nouvellesFormationsFavorites,
    });
  };

  const mettreÀJourUneCommuneFavorite = async (commune: CommuneFavorite) => {
    if (!élève) return;

    const communesFavoritesExistantes = élève.communesFavorites ?? [];
    const estFavorite = communesFavoritesExistantes.some(
      (communeFavorite) => communeFavorite.codeInsee === commune.codeInsee,
    );

    if (estFavorite) {
      const nouvellesCommunesFavorites = communesFavoritesExistantes.filter(
        (communeFavorite) => communeFavorite.codeInsee !== commune.codeInsee,
      );

      await mettreÀJourÉlève({ communesFavorites: nouvellesCommunesFavorites });
    } else {
      await mettreÀJourÉlève({ communesFavorites: [...communesFavoritesExistantes, commune] });
    }
  };

  const mettreÀJourUnVoeu = async (voeu: VoeuFavori) => {
    if (!élève) return;

    const estFavori = élève?.voeuxFavoris?.some((voeuFavori) => voeuFavori.id === voeu.id) ?? false;

    if (estFavori) {
      await mettreÀJourÉlève({
        voeuxFavoris: élève.voeuxFavoris?.filter((voeuFavori) => voeuFavori.id !== voeu.id),
      });
    } else {
      await mettreÀJourÉlève({
        voeuxFavoris: [...(élève.voeuxFavoris ?? []), voeu],
      });
    }
  };

  return {
    élève,
    mettreÀJourÉlève,
    mettreÀJourUneFormationFavorite,
    mettreÀJourUneCommuneFavorite,
    mettreÀJourUnVoeu,
  };
}
