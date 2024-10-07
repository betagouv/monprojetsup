import { type CommentaireVoeuxProps } from "./CommentaireVoeux.interface";
import useCommentaireVoeux from "./useCommentaireVoeux";
import Bouton from "@/components/Bouton/Bouton";
import ChampZoneDeTexte from "@/components/ChampZoneDeTexte/ChampZoneDeTexte";
import { i18n } from "@/configuration/i18n/i18n";

const CommentaireVoeux = ({ formationId }: CommentaireVoeuxProps) => {
  const { enregistrerLeCommentaire, commentaireParDéfaut } = useCommentaireVoeux({ formationId });

  return (
    <form
      className="grid sm:grid-cols-[1fr_auto] sm:gap-4 sm:marker:items-start"
      onSubmit={enregistrerLeCommentaire}
    >
      <ChampZoneDeTexte
        entête={{
          label: i18n.PAGE_FORMATION.VOEUX.COMMENTAIRE.LABEL,
        }}
        id="commentairevoeux"
        valeurParDéfaut={commentaireParDéfaut}
      />
      <div className="sm:mt-7">
        <Bouton
          label={i18n.PAGE_FORMATION.VOEUX.COMMENTAIRE.BOUTON}
          type="submit"
          variante="tertiaire"
        />
      </div>
    </form>
  );
};

export default CommentaireVoeux;
