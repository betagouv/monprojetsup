import { type ChoixFormationProps } from "./ChoixFormation.interface.tsx";
import Voeux from "./Voeux/Voeux";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import Ambition from "@/features/formation/ui/FicheFormation/ChoixFormation/Ambition/Ambition.tsx";
import Commentaire from "@/features/formation/ui/FicheFormation/ChoixFormation/Commentaire/Commentaire.tsx";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const ChoixFormation = ({ formation }: ChoixFormationProps) => {
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
          {i18n.PAGE_FORMATION.CHOIX.TITRE}
        </Titre>
      </div>
      <Ambition
        ambitionActuelle={détailFavori?.niveauAmbition}
        formationId={formation.id}
      />
      <hr className="pb-[1px]" />
      <Voeux formation={formation} />
      <hr className="pb-[1px]" />
      <Commentaire formationId={formation.id} />
    </div>
  );
};

export default ChoixFormation;
