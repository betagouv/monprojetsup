import { NotePersonnelleFormElement } from "./NotePersonnelleFormation.interface";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { type ChampZoneDeTexteProps } from "@/components/ChampZoneDeTexte/ChampZoneDeTexte.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève.ts";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation.ts";
import { useMemo, useState } from "react";

export default function useNotePersonnelleFormation() {
  const [status, setStatus] = useState<ChampZoneDeTexteProps["status"]>();
  const { élève } = useÉlève();
  const { mettreÀJourNotesPersonnellesÉlève } = useÉlèveMutation();
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const notePersonnelle = useMemo(() => {
    if (!élève) return "";

    return élève.notesPersonnelles?.find((note) => note.idFormation === formationAffichée.id)?.note ?? "";
  }, [formationAffichée, élève]);

  const enregistrerNotePersonnelle = async (event: React.FormEvent<NotePersonnelleFormElement>) => {
    event.preventDefault();

    if (!élève || !formationAffichée.id) return;

    if (
      event.currentTarget.elements.notePersonnelle.value.length >=
      constantes.FORMATIONS.FICHES.NB_CARACTÈRES_MAX_NOTE_PERSONNELLE
    ) {
      setStatus({
        type: "erreur",
        message: `${i18n.COMMUN.ERREURS_FORMULAIRES.MOINS_DE_X_CARACTÈRES} ${constantes.FORMATIONS.FICHES.NB_CARACTÈRES_MAX_NOTE_PERSONNELLE} ${i18n.COMMUN.ERREURS_FORMULAIRES.CARACTÈRES}`,
      });
      return;
    }

    setStatus(undefined);

    await mettreÀJourNotesPersonnellesÉlève([
      { idFormation: formationAffichée.id, note: event.currentTarget.elements.notePersonnelle.value },
    ]);
  };

  return {
    enregistrerNotePersonnelle,
    notePersonnelle,
    status,
  };
}
