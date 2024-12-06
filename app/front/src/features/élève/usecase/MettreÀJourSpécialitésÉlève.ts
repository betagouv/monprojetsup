import { type Élève, SpécialitéÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourSpécialitésÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, idsSpécialitésÀModifier: SpécialitéÉlève[]): Promise<Élève | Error> {
    const spécialités = new Set(élève.spécialités);

    for (const idSpécialité of idsSpécialitésÀModifier) {
      if (spécialités.has(idSpécialité)) {
        spécialités.delete(idSpécialité);
      } else {
        spécialités.add(idSpécialité);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, spécialités: [...spécialités] });
  }
}
