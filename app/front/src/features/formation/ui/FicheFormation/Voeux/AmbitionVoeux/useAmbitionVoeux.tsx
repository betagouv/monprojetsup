import { type useAmbitionVoeuxArgs } from "./AmbitionVoeux.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { type Élève, type FormationFavorite } from "@/features/élève/domain/élève.interface";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function useAmbitionVoeux({ formationId }: useAmbitionVoeuxArgs) {
  const { data: élève } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: ["mettreÀJourÉlève"] });

  const ambitions: Array<{ niveau: NonNullable<FormationFavorite["niveauAmbition"]>; emoji: string; libellé: string }> =
    [
      {
        niveau: 1,
        emoji: i18n.PAGE_FORMATION.VOEUX.AMBITIONS.PLAN_B.EMOJI,
        libellé: i18n.PAGE_FORMATION.VOEUX.AMBITIONS.PLAN_B.LABEL,
      },
      {
        niveau: 2,
        emoji: i18n.PAGE_FORMATION.VOEUX.AMBITIONS.RÉALISTE.EMOJI,
        libellé: i18n.PAGE_FORMATION.VOEUX.AMBITIONS.RÉALISTE.LABEL,
      },
      {
        niveau: 3,
        emoji: i18n.PAGE_FORMATION.VOEUX.AMBITIONS.AMBITIEUX.EMOJI,
        libellé: i18n.PAGE_FORMATION.VOEUX.AMBITIONS.AMBITIEUX.LABEL,
      },
    ];

  const mettreAJourAmbition = (niveauAmbition: FormationFavorite["niveauAmbition"]) => {
    if (!élève) return;

    const nouvellesFormationsFavorites =
      élève.formationsFavorites?.map((formationFavorite) => {
        if (formationFavorite.id === formationId) {
          const nouveauNiveauAmbition = niveauAmbition === formationFavorite.niveauAmbition ? null : niveauAmbition;

          return { ...formationFavorite, niveauAmbition: nouveauNiveauAmbition };
        }

        return formationFavorite;
      }) ?? [];

    mutationÉlève.mutateAsync({
      ...élève,
      formationsFavorites: nouvellesFormationsFavorites,
    });
  };

  return {
    ambitions,
    mettreAJourAmbition,
    key: JSON.stringify(élève?.formationsFavorites),
  };
}
