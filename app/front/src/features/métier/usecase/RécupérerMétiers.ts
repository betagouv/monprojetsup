import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class RécupérerMétiersUseCase {
  public constructor(private readonly _métierRepository: MétierRepository) {}

  public async run(métierIds: Array<Métier["id"]>): Promise<Métier[] | Error> {
    return await this._métierRepository.récupérerPlusieurs(métierIds);
  }
}
