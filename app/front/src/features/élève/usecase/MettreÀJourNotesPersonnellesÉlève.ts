import { type Élève, NotePersonnelleFormationÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourNotesPersonnellesÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, notesPersonnellesÀModifier: NotePersonnelleFormationÉlève[]): Promise<Élève | Error> {
    const notesPersonnelles = new Map(
      élève.notesPersonnelles?.map((notePersonnelle) => [notePersonnelle.idFormation, notePersonnelle]),
    );

    for (const notePersonnelleÀModifier of notesPersonnellesÀModifier) {
      if (notesPersonnelles.has(notePersonnelleÀModifier.idFormation) && notePersonnelleÀModifier.note === "") {
        notesPersonnelles.delete(notePersonnelleÀModifier.idFormation);
      } else {
        notesPersonnelles.set(notePersonnelleÀModifier.idFormation, notePersonnelleÀModifier);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({
      ...élève,
      notesPersonnelles: [...notesPersonnelles.values()],
    });
  }
}
