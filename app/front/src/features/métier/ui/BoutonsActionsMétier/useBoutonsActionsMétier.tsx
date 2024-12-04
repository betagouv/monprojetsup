import { type useBoutonsActionsMétierArgs } from "./BoutonsActionsMétier.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { mutationÉlèveKeys, élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function useBoutonsActionsMétier({ métier }: useBoutonsActionsMétierArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: [mutationÉlèveKeys.PROFIL] });

  const estFavori = élève?.métiersFavoris?.some((métierFavori) => métierFavori === métier.id) ?? false;

  const modifierMétiersFavoris = async (métiersFavoris: Élève["métiersFavoris"]) => {
    if (!élève) return;

    await mutationÉlève.mutateAsync({
      ...élève,
      métiersFavoris,
    });
  };

  const ajouterEnFavori = async () => {
    const métiersFavoris = élève?.métiersFavoris ?? [];

    await modifierMétiersFavoris([métier.id, ...métiersFavoris]);
  };

  const supprimerDesFavoris = async () => {
    const métiersFavoris = élève?.métiersFavoris?.filter((métiersFavori) => métiersFavori !== métier.id);

    await modifierMétiersFavoris(métiersFavoris ?? []);
  };

  return {
    estFavori,
    ajouterEnFavori,
    supprimerDesFavoris,
  };
}
