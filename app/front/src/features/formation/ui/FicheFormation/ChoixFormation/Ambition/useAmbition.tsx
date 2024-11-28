import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu.ts";
import { i18n } from "@/configuration/i18n/i18n";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useAmbition() {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const ambitionActuelle =
    élève?.formationsFavorites?.find((formationFavorite) => formationAffichée.id === formationFavorite.id)
      ?.niveauAmbition ?? null;

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
    if (!élève || !formationAffichée.id) return;

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationAffichée.id);

    if (formationFavorite && niveauAmbition === formationFavorite.niveauAmbition) {
      void mettreÀJourUneFormationFavorite(formationAffichée.id, { niveauAmbition: null });
    } else {
      void mettreÀJourUneFormationFavorite(formationAffichée.id, { niveauAmbition });
    }
  };

  return {
    ambitionActuelle,
    ambitions,
    mettreAJourAmbition,
    key: JSON.stringify(élève?.formationsFavorites),
  };
}
