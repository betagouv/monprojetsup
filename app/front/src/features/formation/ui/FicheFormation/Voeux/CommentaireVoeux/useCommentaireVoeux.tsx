import { type CommentaireVoeuxFormElement, type UseCommentaireVoeuxArgs } from "./CommentaireVoeux.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo } from "react";

export default function useCommentaireVoeux({ formationId }: UseCommentaireVoeuxArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

  const commentaireParDéfaut = useMemo(() => {
    if (!élève) return "";

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationId);

    return formationFavorite?.commentaire ?? "";
  }, [formationId, élève]);

  const enregistrerLeCommentaire = (event: React.FormEvent<CommentaireVoeuxFormElement>) => {
    event.preventDefault();

    if (!élève) return;

    mettreÀJourUneFormationFavorite(formationId, {
      commentaire: event.currentTarget.elements.commentairevoeux.value,
    });
  };

  return {
    enregistrerLeCommentaire,
    commentaireParDéfaut,
  };
}
