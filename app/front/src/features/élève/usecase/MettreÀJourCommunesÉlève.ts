import { CommuneÉlève, type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourCommunesÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, communesÀModifier: CommuneÉlève[]): Promise<Élève | Error> {
    const communes = new Map(élève.communesFavorites?.map((commune) => [commune.codeInsee, commune]));

    for (const commune of communesÀModifier) {
      if (communes.has(commune.codeInsee)) {
        communes.delete(commune.codeInsee);
      } else {
        communes.set(commune.codeInsee, commune);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, communesFavorites: [...communes.values()] });
  }
}
