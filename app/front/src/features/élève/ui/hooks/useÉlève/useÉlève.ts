import { type UseÉlèveArgs } from "./useÉlève.interface";
import { type FormationFavorite, type Élève } from "@/features/élève/domain/élève.interface";
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

  const ajouterUnVoeuFavori = async (voeuId: string) => {
    if (!élève) return;
    const voeuxActuels = élève.voeuxFavoris ?? [];
    const nouveauvoeu = { id: voeuId, estParcoursup: false };
    const nouveauxVoeux = [...voeuxActuels, nouveauvoeu];

    await mettreÀJourÉlève({
      voeuxFavoris: nouveauxVoeux,
    });
  };

  const supprimerUnVoeuFavori = async (voeuId: string) => {
    if (!élève) return;
    const voeuxActuels = élève.voeuxFavoris ?? [];
    const nouveauxVoeux = voeuxActuels.filter((voeu) => voeu.id !== voeuId);

    await mettreÀJourÉlève({
      voeuxFavoris: [...nouveauxVoeux],
    });
  };

  return {
    élève,
    mettreÀJourÉlève,
    mettreÀJourUneFormationFavorite,
    ajouterUnVoeuFavori,
    supprimerUnVoeuFavori,
  };
}
