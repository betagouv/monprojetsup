import { UseÉlèveMutationArgs } from "./useÉlèveMutation.interface";
import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import {
  AmbitionFormationÉlève,
  CommuneÉlève,
  type Élève,
  FormationÉlève,
  FormationMasquéeÉlève,
  MétierÉlève,
  NotePersonnelleFormationÉlève,
  SpécialitéÉlève,
  VoeuÉlève,
} from "@/features/élève/domain/élève.interface";
import { queryÉlèveKeys } from "@/features/élève/ui/élèveQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMutation } from "@tanstack/react-query";

const mutationHandler = async (mettreÀJourCallback: () => Promise<Error | Élève>) => {
  const réponse = await mettreÀJourCallback();

  if (réponse instanceof Error) throw réponse;

  return réponse;
};

export default function useÉlèveMutation({ àLaMiseÀJourÉlèveAvecSuccès }: UseÉlèveMutationArgs = {}) {
  const { élève } = useÉlève();

  const miseÀJourÉlèveAvecSuccès = async (profilÉlève?: Élève) => {
    if (profilÉlève) {
      queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
      await àLaMiseÀJourÉlèveAvecSuccès?.();
    }
  };

  const mutationProfilÉlève = useMutation({
    mutationKey: ["mettreÀJourProfilÉlève"],
    mutationFn: async (
      modificationsProfilÉlève: Parameters<typeof dépendances.mettreÀJourProfilÉlèveUseCase.run>["1"],
    ) => {
      if (!élève) return undefined;

      return await mutationHandler(() =>
        dépendances.mettreÀJourProfilÉlèveUseCase.run(élève, modificationsProfilÉlève),
      );
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationSpécialitésÉlève = useMutation({
    mutationKey: ["mettreÀJourSpécialitésÉlève"],
    mutationFn: async (spécialitésÀModifier: SpécialitéÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.mettreÀJourSpécialitésÉlèveUseCase.run(élève, spécialitésÀModifier));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationVoeuxÉlève = useMutation({
    mutationKey: ["mettreÀJourVoeuxÉlève"],
    mutationFn: async (voeuxÀModifier: VoeuÉlève["id"][]) => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.mettreÀJourVoeuxÉlèveUseCase.run(élève, voeuxÀModifier));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationCommunesÉlève = useMutation({
    mutationKey: ["mettreÀJourCommunesÉlève"],
    mutationFn: async (communesÀModifier: CommuneÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.mettreÀJourCommunesÉlèveUseCase.run(élève, communesÀModifier));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationFormationsÉlève = useMutation({
    mutationKey: ["mettreÀJourFormationsÉlève"],
    mutationFn: async (formationsÀModifier: FormationÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.mettreÀJourFormationsÉlèveUseCase.run(élève, formationsÀModifier));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationMétiersÉlève = useMutation({
    mutationKey: ["mettreÀJourMétiersÉlève"],
    mutationFn: async (métiersÀModifier: MétierÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.mettreÀJourMétiersÉlèveUseCase.run(élève, métiersÀModifier));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationFormationsMasquéesÉlève = useMutation({
    mutationKey: ["mettreÀJourFormationsMasquéesÉlève"],
    mutationFn: async (formationsMasquéesÀModifier: FormationMasquéeÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() =>
        dépendances.mettreÀJourFormationsMasquéesÉlèveUseCase.run(élève, formationsMasquéesÀModifier),
      );
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationNotesPersonnellesÉlève = useMutation({
    mutationKey: ["mettreÀJourNotesPersonnellesÉlève"],
    mutationFn: async (notesPersonnellesÀModifier: NotePersonnelleFormationÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() =>
        dépendances.mettreÀJourNotesPersonnellesÉlèveUseCase.run(élève, notesPersonnellesÀModifier),
      );
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const mutationAmbitionsÉlève = useMutation({
    mutationKey: ["mettreÀJourAmbitionsÉlève"],
    mutationFn: async (ambitionsÀModifier: AmbitionFormationÉlève[]) => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.mettreÀJourAmbitionsÉlèveUseCase.run(élève, ambitionsÀModifier));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const supprimerTousLesMétiersÉlève = useMutation({
    mutationKey: ["supprimerTousLesMétiersÉlève"],
    mutationFn: async () => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.supprimerTousLesMétiersÉlèveUseCase.run(élève));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  const supprimerToutesLesFormationsÉlève = useMutation({
    mutationKey: ["supprimerTousLesMétiersÉlève"],
    mutationFn: async () => {
      if (!élève) return undefined;

      return mutationHandler(() => dépendances.supprimerToutesLesFormationsÉlèveUseCase.run(élève));
    },
    onSuccess: miseÀJourÉlèveAvecSuccès,
  });

  return {
    mettreÀJourProfilÉlève: mutationProfilÉlève.mutateAsync,
    mettreÀJourFormationsÉlève: mutationFormationsÉlève.mutateAsync,
    mettreÀJourMétiersÉlève: mutationMétiersÉlève.mutateAsync,
    mettreÀJourSpécialitésÉlève: mutationSpécialitésÉlève.mutateAsync,
    mettreÀJourVoeuxÉlève: mutationVoeuxÉlève.mutateAsync,
    mettreÀJourCommunesÉlève: mutationCommunesÉlève.mutateAsync,
    mettreÀJourFormationsMasquéesÉlève: mutationFormationsMasquéesÉlève.mutateAsync,
    mettreÀJourNotesPersonnellesÉlève: mutationNotesPersonnellesÉlève.mutateAsync,
    mettreÀJourAmbitionsÉlève: mutationAmbitionsÉlève.mutateAsync,
    supprimerTousLesMétiersÉlève: supprimerTousLesMétiersÉlève.mutateAsync,
    supprimerToutesLesFormationsÉlève: supprimerToutesLesFormationsÉlève.mutateAsync,
  };
}
