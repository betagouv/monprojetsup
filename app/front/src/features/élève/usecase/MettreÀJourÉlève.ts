import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(données: Partial<Omit<Élève, "id">>): Promise<Élève | undefined> {
    return await this._élèveRepository.mettreÀJour(données);
  }
}
