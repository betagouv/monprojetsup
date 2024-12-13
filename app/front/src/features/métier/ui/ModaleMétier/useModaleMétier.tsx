import { UseModaleMétierArgs } from "./ModaleMétier.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useBoutonsActionsMétier from "@/features/métier/ui/BoutonsActionsMétier/useBoutonsActionsMétier";
import { ModalProps } from "@codegouvfr/react-dsfr/Modal";
import { useMemo } from "react";

export default function useModaleMétier({ métier }: UseModaleMétierArgs) {
  const { estFavori, mettreÀJourMétiersÉlève } = useBoutonsActionsMétier({
    métier,
  });

  const boutons = useMemo((): ModalProps["buttons"] => {
    const boutonFermer: ModalProps["buttons"] = {
      children: i18n.COMMUN.FERMER,
      priority: "tertiary",
      size: "large",
    };

    if (!estFavori) {
      return [
        boutonFermer,
        {
          children: i18n.COMMUN.AJOUTER_À_MA_SÉLECTION,
          iconId: "fr-icon-heart-line",
          iconPosition: "left",
          size: "large",
          onClick: () => mettreÀJourMétiersÉlève([métier.id]),
          doClosesModal: false,
        },
      ];
    }

    return [
      boutonFermer,
      {
        children: i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION,
        iconId: "fr-icon-close-line",
        iconPosition: "left",
        priority: "secondary",
        size: "large",
        onClick: () => mettreÀJourMétiersÉlève([métier.id]),
        doClosesModal: false,
      },
    ];
  }, [estFavori, mettreÀJourMétiersÉlève, métier.id]);

  const titre = useMemo(() => {
    if (!estFavori) return métier.nom;

    return (
      <>
        {métier.nom}
        <span
          aria-hidden="true"
          className="fr-icon-heart-fill ml-2 text-[--artwork-minor-red-marianne]"
        />
        <span className="sr-only">{i18n.ACCESSIBILITÉ.FAVORI}</span>
      </>
    );
  }, [estFavori, métier]);

  return {
    boutons,
    titre,
  };
}
