import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import {
  CommuneÉlève,
  FormationÉlève,
  MétierÉlève,
  SpécialitéÉlève,
  VoeuÉlève,
  type Élève,
} from "@/features/élève/domain/élève.interface";
import { queryOptions } from "@tanstack/react-query";

export const mutationÉlèveKeys = {
  PROFIL: "mettreÀJourÉlève",
  SPÉCIALITÉS: "mettreÀJourSpécialitésÉlève",
  VOEUX: "mettreÀJourVoeuxÉlève",
  COMMUNES: "mettreÀJourCommunesÉlève",
  FORMATIONS: "mettreÀJourFormationsFavoritesÉlève",
  MÉTIERS: "mettreÀJourMétiersFavorisÉlève",
};

export const queryÉlèveKeys = {
  PROFIL: "élève",
};

export const élèveQueryOptions = queryOptions({
  queryKey: [queryÉlèveKeys.PROFIL],
  queryFn: async () => {
    const réponse = await dépendances.récupérerProfilÉlèveUseCase.run();

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
});

queryClient.setMutationDefaults([mutationÉlèveKeys.PROFIL], {
  mutationFn: async (élève: Élève) => {
    const réponse = await dépendances.mettreÀJourProfilÉlèveUseCase.run(élève);

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
  onSuccess: (profilÉlève) => {
    queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
  },
});

queryClient.setMutationDefaults([mutationÉlèveKeys.SPÉCIALITÉS], {
  mutationFn: async ({
    élève,
    idsSpécialitésÀModifier,
  }: {
    élève: Élève;
    idsSpécialitésÀModifier: SpécialitéÉlève[];
  }) => {
    const réponse = await dépendances.mettreÀJourSpécialitésÉlèveUseCase.run(élève, idsSpécialitésÀModifier);

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
  onSuccess: (profilÉlève) => {
    queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
  },
});

queryClient.setMutationDefaults([mutationÉlèveKeys.VOEUX], {
  mutationFn: async ({ élève, idsVoeuxÀModifier }: { élève: Élève; idsVoeuxÀModifier: VoeuÉlève["id"][] }) => {
    const réponse = await dépendances.mettreÀJourVoeuxÉlèveUseCase.run(élève, idsVoeuxÀModifier);

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
  onSuccess: (profilÉlève) => {
    queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
  },
});

queryClient.setMutationDefaults([mutationÉlèveKeys.COMMUNES], {
  mutationFn: async ({ élève, communesÀModifier }: { élève: Élève; communesÀModifier: CommuneÉlève[] }) => {
    const réponse = await dépendances.mettreÀJourCommunesÉlèveUseCase.run(élève, communesÀModifier);

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
  onSuccess: (profilÉlève) => {
    queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
  },
});

queryClient.setMutationDefaults([mutationÉlèveKeys.FORMATIONS], {
  mutationFn: async ({
    élève,
    idsFormationsÀModifier,
  }: {
    élève: Élève;
    idsFormationsÀModifier: FormationÉlève["id"][];
  }) => {
    const réponse = await dépendances.mettreÀJourFormationsFavoritesÉlèveUseCase.run(élève, idsFormationsÀModifier);

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
  onSuccess: (profilÉlève) => {
    queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
  },
});

queryClient.setMutationDefaults([mutationÉlèveKeys.MÉTIERS], {
  mutationFn: async ({
    élève,
    idsMétiersFavorisÀModifier,
  }: {
    élève: Élève;
    idsMétiersFavorisÀModifier: MétierÉlève[];
  }) => {
    const réponse = await dépendances.mettreÀJourMétiersFavorisÉlèveUseCase.run(élève, idsMétiersFavorisÀModifier);

    if (réponse instanceof Error) throw réponse;

    return réponse;
  },
  onSuccess: (profilÉlève) => {
    queryClient.setQueryData([queryÉlèveKeys.PROFIL], profilÉlève);
  },
});
