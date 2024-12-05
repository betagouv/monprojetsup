import { type UseÉlèveArgs } from "./useÉlève.interface";
import { Formation } from "@/features/formation/domain/formation.interface.ts";
import {
  CommuneFavorite,
  type FormationFavorite,
  IdSpécialité,
  VoeuFavori,
  type Élève,
} from "@/features/élève/domain/élève.interface";
import { mutationÉlèveKeys, élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function useÉlève({ àLaSoumissionDuFormulaireAvecSuccès }: UseÉlèveArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: [mutationÉlèveKeys.PROFIL] });
  const mutationSpécialitésÉlève = useMutation<
    Élève,
    unknown,
    { élève: Élève; idsSpécialitésÀModifier: IdSpécialité[] }
  >({ mutationKey: [mutationÉlèveKeys.SPÉCIALITÉS] });
  const mutationVoeuxÉlève = useMutation<Élève, unknown, { élève: Élève; idsVoeuxÀModifier: VoeuFavori["id"][] }>({
    mutationKey: [mutationÉlèveKeys.VOEUX],
  });
  const mutationCommunesÉlève = useMutation<Élève, unknown, { élève: Élève; communesÀModifier: CommuneFavorite[] }>({
    mutationKey: [mutationÉlèveKeys.COMMUNES],
  });
  const mutationFormationsFavoritesÉlève = useMutation<
    Élève,
    unknown,
    { élève: Élève; idsFormationsÀModifier: Formation["id"][] }
  >({
    mutationKey: [mutationÉlèveKeys.FORMATIONS],
  });

  const mettreÀJourÉlève = async (changements: Partial<Élève>) => {
    if (!élève) return;

    await mutationÉlève.mutateAsync({ ...élève, ...changements });
    àLaSoumissionDuFormulaireAvecSuccès?.();
  };

  const mettreÀJourSpécialitésÉlève = async (idsSpécialitésÀModifier: IdSpécialité[]) => {
    if (!élève) return;

    await mutationSpécialitésÉlève.mutateAsync({ élève, idsSpécialitésÀModifier });
  };

  const mettreÀJourVoeuxÉlève = async (idsVoeuxÀModifier: VoeuFavori["id"][]) => {
    if (!élève) return;

    await mutationVoeuxÉlève.mutateAsync({ élève, idsVoeuxÀModifier });
  };

  const mettreÀJourCommunesÉlève = async (communesÀModifier: CommuneFavorite[]) => {
    if (!élève) return;

    await mutationCommunesÉlève.mutateAsync({ élève, communesÀModifier });
  };

  const mettreÀJourFormationsFavoritesÉlève = async (idsFormationsÀModifier: Formation["id"][]) => {
    if (!élève) return;

    await mutationFormationsFavoritesÉlève.mutateAsync({ élève, idsFormationsÀModifier });
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

  return {
    élève,
    mettreÀJourÉlève,
    mettreÀJourUneFormationFavorite,
    mettreÀJourFormationsFavoritesÉlève,
    mettreÀJourSpécialitésÉlève,
    mettreÀJourVoeuxÉlève,
    mettreÀJourCommunesÉlève,
  };
}
