import { type RéférentielDonnées } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type RéférentielDonnéesRepository } from "@/features/référentielDonnées/infrastructure/référentielDonnéesRepository.interface";

export class RécupérerRéférentielDonnéesUseCase {
  public constructor(private readonly _référentielDonnéesRepository: RéférentielDonnéesRepository) {}

  public async run(): Promise<RéférentielDonnées | Error> {
    return await this._référentielDonnéesRepository.récupérer();
  }
}
