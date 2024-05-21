import { type Bac, type Spécialité } from "@/features/bac/domain/bac.interface";
import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";

export class RechercherSpécialitésPourUnBacUseCase {
  public constructor(private readonly _bacRepository: BacRepository) {}

  public async run(bacId: Bac["id"], recherche?: string): Promise<Spécialité[] | undefined> {
    const spécialités = await this._bacRepository.rechercherSpécialitésDUnBac(bacId, recherche);
    return spécialités?.slice(0, 20);
  }
}
