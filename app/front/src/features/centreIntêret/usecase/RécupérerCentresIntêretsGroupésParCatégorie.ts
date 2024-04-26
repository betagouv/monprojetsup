import { type CatégorieCentresIntêrets } from "@/features/centreIntêret/domain/centreIntêret.interface";
import { type CentreIntêretRepository } from "@/features/centreIntêret/infrastructure/centreIntêretRepository.interface";

export class RécupérerCentresIntêretsGroupésParCatégorieUseCase {
  public constructor(private readonly _centreIntêretRepository: CentreIntêretRepository) {}

  public async run(): Promise<CatégorieCentresIntêrets[] | undefined> {
    return await this._centreIntêretRepository.récupérerTousGroupésParCatégorie();
  }
}
