import { type useAmbitionArgs } from "./Ambition.interface.tsx";
import { i18n } from "@/configuration/i18n/i18n";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useAmbition({ formationId }: useAmbitionArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

  const ambitions: Array<{ niveau: NonNullable<FormationFavorite["niveauAmbition"]>; emoji: string; libellé: string }> =
    [
      {
        niveau: 1,
        emoji: i18n.PAGE_FORMATION.CHOIX.AMBITIONS.PLAN_B.EMOJI,
        libellé: i18n.PAGE_FORMATION.CHOIX.AMBITIONS.PLAN_B.LABEL,
      },
      {
        niveau: 2,
        emoji: i18n.PAGE_FORMATION.CHOIX.AMBITIONS.RÉALISTE.EMOJI,
        libellé: i18n.PAGE_FORMATION.CHOIX.AMBITIONS.RÉALISTE.LABEL,
      },
      {
        niveau: 3,
        emoji: i18n.PAGE_FORMATION.CHOIX.AMBITIONS.AMBITIEUX.EMOJI,
        libellé: i18n.PAGE_FORMATION.CHOIX.AMBITIONS.AMBITIEUX.LABEL,
      },
    ];

  const mettreAJourAmbition = (niveauAmbition: FormationFavorite["niveauAmbition"]) => {
    if (!élève) return;

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationId);

    if (formationFavorite && niveauAmbition === formationFavorite.niveauAmbition) {
      void mettreÀJourUneFormationFavorite(formationId, { niveauAmbition: null });
    } else {
      void mettreÀJourUneFormationFavorite(formationId, { niveauAmbition });
    }
  };

  return {
    ambitions,
    mettreAJourAmbition,
    key: JSON.stringify(élève?.formationsFavorites),
  };
}
