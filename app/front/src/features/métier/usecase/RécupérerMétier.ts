import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class RécupérerMétierUseCase {
  public constructor(private readonly _métierRepository: MétierRepository) {}

  public async run(métierId: Métier["id"]): Promise<Métier | undefined> {
    return await this._métierRepository.récupérer(métierId);
  }
}
