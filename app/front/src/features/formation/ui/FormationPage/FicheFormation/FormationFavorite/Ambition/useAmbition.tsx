import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { i18n } from "@/configuration/i18n/i18n";
import { AmbitionFormationÉlève } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { useMemo } from "react";

export default function useAmbition() {
  const { élève } = useÉlève();
  const { mettreÀJourAmbitionsÉlève } = useÉlèveMutation();
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const ambitionActuelle = useMemo(() => {
    if (!élève) return "";

    return élève.ambitions?.find((ambition) => ambition.idFormation === formationAffichée.id)?.ambition ?? null;
  }, [formationAffichée, élève]);

  const ambitions: Array<{ niveau: NonNullable<AmbitionFormationÉlève["ambition"]>; emoji: string; libellé: string }> =
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

  const mettreAJourAmbition = async (niveauAmbition: AmbitionFormationÉlève["ambition"]) => {
    if (!formationAffichée.id) return;

    if (niveauAmbition === ambitionActuelle) {
      await mettreÀJourAmbitionsÉlève([{ idFormation: formationAffichée.id, ambition: null }]);
    } else {
      await mettreÀJourAmbitionsÉlève([{ idFormation: formationAffichée.id, ambition: niveauAmbition }]);
    }
  };

  return {
    ambitionActuelle,
    ambitions,
    mettreAJourAmbition,
    key: JSON.stringify(élève?.ambitions),
  };
}
