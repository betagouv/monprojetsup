import useNotePersonnelleFormation from "./useNotePersonnelleFormation.tsx";
import Bouton from "@/components/Bouton/Bouton";
import ChampZoneDeTexte from "@/components/ChampZoneDeTexte/ChampZoneDeTexte";
import { i18n } from "@/configuration/i18n/i18n";

const NotePersonnelleFormation = () => {
  const { enregistrerNotePersonnelle, notePersonnelle, status } = useNotePersonnelleFormation();

  return (
    <form
      className="grid sm:grid-cols-[1fr_auto] sm:gap-4 sm:marker:items-start"
      onSubmit={enregistrerNotePersonnelle}
    >
      <ChampZoneDeTexte
        entête={{
          label: i18n.PAGE_FORMATION.CHOIX.NOTE_PERSONNELLE.LABEL,
        }}
        id="notePersonnelle"
        status={status}
        valeurParDéfaut={notePersonnelle}
      />
      <div className="sm:mt-7">
        <Bouton
          type="submit"
          variante="tertiaire"
        >
          {i18n.PAGE_FORMATION.CHOIX.NOTE_PERSONNELLE.BOUTON}
        </Bouton>
      </div>
    </form>
  );
};

export default NotePersonnelleFormation;
