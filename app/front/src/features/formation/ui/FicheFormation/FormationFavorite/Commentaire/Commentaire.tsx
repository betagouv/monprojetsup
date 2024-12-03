import useCommentaire from "./useCommentaire.tsx";
import Bouton from "@/components/Bouton/Bouton";
import ChampZoneDeTexte from "@/components/ChampZoneDeTexte/ChampZoneDeTexte";
import { i18n } from "@/configuration/i18n/i18n";

const Commentaire = () => {
  const { enregistrerLeCommentaire, commentaireParDéfaut, status } = useCommentaire();

  return (
    <form
      className="grid sm:grid-cols-[1fr_auto] sm:gap-4 sm:marker:items-start"
      onSubmit={enregistrerLeCommentaire}
    >
      <ChampZoneDeTexte
        entête={{
          label: i18n.PAGE_FORMATION.CHOIX.COMMENTAIRE.LABEL,
        }}
        id="commentaire"
        status={status}
        valeurParDéfaut={commentaireParDéfaut}
      />
      <div className="sm:mt-7">
        <Bouton
          label={i18n.PAGE_FORMATION.CHOIX.COMMENTAIRE.BOUTON}
          type="submit"
          variante="tertiaire"
        />
      </div>
    </form>
  );
};

export default Commentaire;
