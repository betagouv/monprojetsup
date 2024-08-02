import { type CatégorieCentreIntêret } from "@/features/centreIntêret/domain/centreIntêret.interface";
import { type CentreIntêretRepository } from "@/features/centreIntêret/infrastructure/centreIntêretRepository.interface";

export class RécupérerCatégoriesEtSousCatégoriesCentreIntêretUseCase {
  public constructor(private readonly _centreIntêretRepository: CentreIntêretRepository) {}

  public async run(): Promise<CatégorieCentreIntêret[] | undefined> {
    return await this._centreIntêretRepository.récupérer();
  }
}
