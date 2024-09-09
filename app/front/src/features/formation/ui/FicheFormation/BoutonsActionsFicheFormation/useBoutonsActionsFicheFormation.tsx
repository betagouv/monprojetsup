import { type useBoutonsActionsFicheFormationArgs } from "./BoutonsActionsFicheFormation.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function useBoutonsActionsFicheFormation({ formation }: useBoutonsActionsFicheFormationArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: ["mettreÀJourÉlève"] });

  const estFavorite =
    élève?.formationsFavorites?.some((formationFavorite) => formationFavorite.id === formation.id) ?? false;

  const modifierFormationsFavorites = async (formationsFavorites: Élève["formationsFavorites"]) => {
    if (!élève) return;

    await mutationÉlève.mutateAsync({
      ...élève,
      formationsFavorites,
    });
  };

  const modifierFormationsMasquées = async (formationsMasquées: Élève["formationsMasquées"]) => {
    if (!élève) return;

    await mutationÉlève.mutateAsync({
      ...élève,
      formationsMasquées,
    });
  };

  const ajouterEnFavori = async () => {
    const formationsFavorites = [
      ...(élève?.formationsFavorites ?? []),
      {
        id: formation.id,
        commentaire: null,
        niveauAmbition: null,
        tripletsAffectationsChoisis: [],
      },
    ];

    await modifierFormationsFavorites(formationsFavorites);
  };

  const supprimerDesFavoris = async () => {
    const formationsFavorites = élève?.formationsFavorites?.filter(
      (formationFavorite) => formationFavorite.id !== formation.id,
    );

    await modifierFormationsFavorites(formationsFavorites ?? []);
  };

  const masquerUneFormation = async () => {
    const formationsMasquées = élève?.formationsMasquées ?? [];

    await modifierFormationsMasquées([...formationsMasquées, formation.id]);
  };

  return {
    estFavorite,
    ajouterEnFavori,
    supprimerDesFavoris,
    masquerUneFormation,
  };
}
