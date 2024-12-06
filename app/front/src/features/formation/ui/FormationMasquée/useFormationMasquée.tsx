import { UseFormationMasquéeArgs } from "@/features/formation/ui/FormationMasquée/FormationMasquée.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève.ts";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

export default function useFormationMasquée({ formation }: UseFormationMasquéeArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);

  const { mettreÀJourÉlève } = useÉlève();

  const démasquerUneFormation = async () => {
    const formationsMasquées = élève?.formationsMasquées ?? [];
    const nouvellesFormationsMasquées = formationsMasquées?.filter(
      (idFormationMasquée) => idFormationMasquée !== formation.id,
    );
    await mettreÀJourÉlève({ formationsMasquées: nouvellesFormationsMasquées });
  };

  return {
    démasquerUneFormation,
  };
}
