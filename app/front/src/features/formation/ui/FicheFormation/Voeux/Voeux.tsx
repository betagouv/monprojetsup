import AmbitionVoeux from "./AmbitionVoeux/AmbitionVoeux";
import CommentaireVoeux from "./CommentaireVoeux/CommentaireVoeux";
import ÉtablissementsVoeux from "./ÉtablissementsVoeux/ÉtablissementsVoeux";
import { type VoeuxProps } from "./Voeux.interface";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const Voeux = ({ formation }: VoeuxProps) => {
  const { data: élève } = useQuery(élèveQueryOptions);

  if (!élève) return null;

  const détailFavori = élève.formationsFavorites?.find((formationFavorite) => formation.id === formationFavorite.id);

  return (
    <div className="grid gap-6 border border-solid border-[--border-default-grey] px-4 py-8 shadow-md sm:px-10">
      <div className="*:mb-0 *:text-[--text-label-grey]">
        <Titre
          niveauDeTitre="h2"
          styleDeTitre="h4"
        >
          {i18n.PAGE_FORMATION.VOEUX.TITRE}
        </Titre>
      </div>
      <AmbitionVoeux
        ambitionActuelle={détailFavori?.niveauAmbition}
        formationId={formation.id}
      />
      <hr className="pb-[1px]" />
      <ÉtablissementsVoeux formation={formation} />
      <hr className="pb-[1px]" />
      <CommentaireVoeux formationId={formation.id} />
    </div>
  );
};

export default Voeux;
