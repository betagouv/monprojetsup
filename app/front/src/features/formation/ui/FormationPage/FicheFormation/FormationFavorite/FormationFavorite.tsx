import Ambition from "./Ambition/Ambition";
import NotePersonnelleFormation from "./NotePersonnelleFormation/NotePersonnelleFormation";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import Voeux from "@/features/formation/ui/Voeux/Voeux";

const FormationFavorite = () => {
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { élève } = useÉlève();

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
      <NotePersonnelleFormation />
    </div>
  );
};

export default FormationFavorite;
