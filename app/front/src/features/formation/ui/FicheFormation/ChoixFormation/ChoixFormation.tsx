import Voeux from "./Voeux/Voeux";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu.ts";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import Ambition from "@/features/formation/ui/FicheFormation/ChoixFormation/Ambition/Ambition.tsx";
import Commentaire from "@/features/formation/ui/FicheFormation/ChoixFormation/Commentaire/Commentaire.tsx";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const ChoixFormation = () => {
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: élève } = useQuery(élèveQueryOptions);

  if (!élève || !formationAffichée) return null;

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
      <Ambition />
      <hr className="pb-[1px]" />
      <Voeux />
      <hr className="pb-[1px]" />
      <Commentaire />
    </div>
  );
};

export default ChoixFormation;
