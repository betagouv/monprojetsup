import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class RechercherMétiersUseCase {
  public constructor(private readonly _métierRepository: MétierRepository) {}

  public async run(recherche: string): Promise<Métier[] | Error> {
    return await this._métierRepository.rechercher(recherche);
  }
}
