import { type useBoutonsActionsFicheFormationArgs } from "./BoutonsActionsFicheFormation.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève.ts";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

export default function useBoutonsActionsFicheFormation({ formation }: useBoutonsActionsFicheFormationArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);

  const estFavorite =
    élève?.formationsFavorites?.some((formationFavorite) => formationFavorite.id === formation.id) ?? false;

  const estMasquée =
    élève?.formationsMasquées?.some((formationsMasquée) => formationsMasquée === formation.id) ?? false;

  const { mettreÀJourÉlève } = useÉlève();

  const ajouterEnFavori = async () => {
    const formationsFavorites = [
      {
        id: formation.id,
        commentaire: null,
        niveauAmbition: null,
        voeux: [],
      },
      ...(élève?.formationsFavorites ?? []),
    ];

    await mettreÀJourÉlève({ formationsFavorites });
  };

  const supprimerDesFavoris = async () => {
    const formationsFavorites = élève?.formationsFavorites?.filter(
      (formationFavorite) => formationFavorite.id !== formation.id,
    );

    await mettreÀJourÉlève({ formationsFavorites: formationsFavorites ?? [] });
  };

  const masquerUneFormation = async () => {
    const formationsMasquées = élève?.formationsMasquées ?? [];

    await mettreÀJourÉlève({ formationsMasquées: [formation.id, ...formationsMasquées] });
  };

  const afficherÀNouveauUneFormation = async () => {
    const formationsMasquées = élève?.formationsMasquées?.filter(
      (formationMasquée) => formationMasquée !== formation.id,
    );

    await mettreÀJourÉlève({ formationsMasquées: formationsMasquées ?? [] });
  };

  return {
    estFavorite,
    estMasquée,
    ajouterEnFavori,
    supprimerDesFavoris,
    masquerUneFormation,
    afficherÀNouveauUneFormation,
  };
}
