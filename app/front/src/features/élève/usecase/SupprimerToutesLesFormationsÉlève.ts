import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class SupprimerToutesLesFormationsÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève): Promise<Élève | Error> {
    return await this._élèveRepository.mettreÀJourProfil({ ...élève, formations: [] });
  }
}
