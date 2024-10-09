import { type useAmbitionVoeuxArgs } from "./AmbitionVoeux.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useAmbitionVoeux({ formationId }: useAmbitionVoeuxArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

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

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationId);

    if (formationFavorite && niveauAmbition === formationFavorite.niveauAmbition) {
      mettreÀJourUneFormationFavorite(formationId, { niveauAmbition: null });
    } else {
      mettreÀJourUneFormationFavorite(formationId, { niveauAmbition });
    }
  };

  return {
    ambitions,
    mettreAJourAmbition,
    key: JSON.stringify(élève?.formationsFavorites),
  };
}
