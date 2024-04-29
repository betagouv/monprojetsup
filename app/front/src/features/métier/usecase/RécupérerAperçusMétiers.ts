import { type MétierAperçu } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class RécupérerAperçusMétiersUseCase {
  public constructor(private readonly _métierRepository: MétierRepository) {}

  public async run(métierIds: Array<MétierAperçu["id"]>): Promise<MétierAperçu[] | undefined> {
    return await this._métierRepository.récupérerAperçus(métierIds);
  }
}
