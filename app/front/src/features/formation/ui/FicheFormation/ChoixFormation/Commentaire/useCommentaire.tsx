import { type CommentaireFormElement } from "./Commentaire.interface.tsx";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu.ts";
import { type ChampZoneDeTexteProps } from "@/components/ChampZoneDeTexte/ChampZoneDeTexte.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo, useState } from "react";

export default function useCommentaire() {
  const [status, setStatus] = useState<ChampZoneDeTexteProps["status"]>();
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const commentaireParDéfaut = useMemo(() => {
    if (!élève) return "";

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationAffichée.id);

    return formationFavorite?.commentaire ?? "";
  }, [formationAffichée, élève]);

  const enregistrerLeCommentaire = (event: React.FormEvent<CommentaireFormElement>) => {
    event.preventDefault();

    if (!élève || !formationAffichée.id) return;

    if (
      event.currentTarget.elements.commentaire.value.length >= constantes.FICHE_FORMATION.NB_CARACTÈRES_MAX_COMMENTAIRE
    ) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.MOINS_DE_X_CARACTÈRES} ${constantes.FICHE_FORMATION.NB_CARACTÈRES_MAX_COMMENTAIRE} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
      return;
    }

    setStatus(undefined);

    void mettreÀJourUneFormationFavorite(formationAffichée.id, {
      commentaire: event.currentTarget.elements.commentaire.value,
    });
  };

  return {
    enregistrerLeCommentaire,
    commentaireParDéfaut,
    status,
  };
}
