import { type UseÉlèveArgs } from "./useÉlève.interface";
import {
  CommuneÉlève,
  type FormationÉlève,
  MétierÉlève,
  SpécialitéÉlève,
  VoeuÉlève,
  type Élève,
} from "@/features/élève/domain/élève.interface";
import { mutationÉlèveKeys, élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function useÉlève({ àLaMiseÀJourÉlèveAvecSuccès }: UseÉlèveArgs = {}) {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: [mutationÉlèveKeys.PROFIL] });
  const mutationSpécialitésÉlève = useMutation<
    Élève,
    unknown,
    { élève: Élève; idsSpécialitésÀModifier: SpécialitéÉlève[] }
  >({ mutationKey: [mutationÉlèveKeys.SPÉCIALITÉS] });
  const mutationVoeuxÉlève = useMutation<Élève, unknown, { élève: Élève; idsVoeuxÀModifier: VoeuÉlève["id"][] }>({
    mutationKey: [mutationÉlèveKeys.VOEUX],
  });
  const mutationCommunesÉlève = useMutation<Élève, unknown, { élève: Élève; communesÀModifier: CommuneÉlève[] }>({
    mutationKey: [mutationÉlèveKeys.COMMUNES],
  });
  const mutationFormationsFavoritesÉlève = useMutation<
    Élève,
    unknown,
    { élève: Élève; idsFormationsÀModifier: FormationÉlève["id"][] }
  >({
    mutationKey: [mutationÉlèveKeys.FORMATIONS],
  });
  const mutationMétiersFavorisÉlève = useMutation<
    Élève,
    unknown,
    { élève: Élève; idsMétiersFavorisÀModifier: MétierÉlève[] }
  >({
    mutationKey: [mutationÉlèveKeys.MÉTIERS],
  });

  const mettreÀJourÉlève = async (changements: Partial<Élève>) => {
    if (!élève) return;

    await mutationÉlève.mutateAsync({ ...élève, ...changements });
    await àLaMiseÀJourÉlèveAvecSuccès?.();
  };

  const mettreÀJourSpécialitésÉlève = async (idsSpécialitésÀModifier: SpécialitéÉlève[]) => {
    if (!élève) return;

    await mutationSpécialitésÉlève.mutateAsync({ élève, idsSpécialitésÀModifier });
  };

  const mettreÀJourVoeuxÉlève = async (idsVoeuxÀModifier: VoeuÉlève["id"][]) => {
    if (!élève) return;

    await mutationVoeuxÉlève.mutateAsync({ élève, idsVoeuxÀModifier });
  };

  const mettreÀJourCommunesÉlève = async (communesÀModifier: CommuneÉlève[]) => {
    if (!élève) return;

    await mutationCommunesÉlève.mutateAsync({ élève, communesÀModifier });
  };

  const mettreÀJourFormationsFavoritesÉlève = async (idsFormationsÀModifier: FormationÉlève["id"][]) => {
    if (!élève) return;

    await mutationFormationsFavoritesÉlève.mutateAsync({ élève, idsFormationsÀModifier });
  };

  const mettreÀJourMétiersFavorisÉlève = async (idsMétiersFavorisÀModifier: MétierÉlève[]) => {
    if (!élève) return;

    await mutationMétiersFavorisÉlève.mutateAsync({ élève, idsMétiersFavorisÀModifier });
  };

  const mettreÀJourUneFormationFavorite = async (
    formationId: FormationÉlève["id"],
    changements: Partial<FormationÉlève>,
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
    mettreÀJourMétiersFavorisÉlève,
    mettreÀJourSpécialitésÉlève,
    mettreÀJourVoeuxÉlève,
    mettreÀJourCommunesÉlève,
  };
}
