import { type CommentaireVoeuxFormElement, type UseCommentaireVoeuxArgs } from "./CommentaireVoeux.interface";
import { type ChampZoneDeTexteProps } from "@/components/ChampZoneDeTexte/ChampZoneDeTexte.interface";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo, useState } from "react";

export default function useCommentaireVoeux({ formationId }: UseCommentaireVoeuxArgs) {
  const NB_CARACTÈRES_MAX = 4_000;
  const [status, setStatus] = useState<ChampZoneDeTexteProps["status"]>();
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

  const commentaireParDéfaut = useMemo(() => {
    if (!élève) return "";

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationId);

    return formationFavorite?.commentaire ?? "";
  }, [formationId, élève]);

  const enregistrerLeCommentaire = (event: React.FormEvent<CommentaireVoeuxFormElement>) => {
    event.preventDefault();

    if (!élève) return;

    if (event.currentTarget.elements.commentairevoeux.value.length >= NB_CARACTÈRES_MAX) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.MOINS_DE_X_CARACTÈRES} ${NB_CARACTÈRES_MAX} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
      return;
    }

    setStatus(undefined);

    void mettreÀJourUneFormationFavorite(formationId, {
      commentaire: event.currentTarget.elements.commentairevoeux.value,
    });
  };

  return {
    enregistrerLeCommentaire,
    commentaireParDéfaut,
    status,
  };
}
