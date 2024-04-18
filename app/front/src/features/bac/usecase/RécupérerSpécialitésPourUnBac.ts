import { type Bac, type Spécialité } from "@/features/bac/domain/bac.interface";
import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";

export class RécupérerSpécialitésPourUnBacUseCase {
  public constructor(private readonly _bacRepository: BacRepository) {}

  public async run(bacId: Bac["id"]): Promise<Spécialité[] | undefined> {
    return await this._bacRepository.récupérerSpécialités(bacId);
  }
}
