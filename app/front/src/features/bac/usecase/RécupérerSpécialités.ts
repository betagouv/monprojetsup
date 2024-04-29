import { type Spécialité } from "@/features/bac/domain/bac.interface";
import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";

export class RécupérerSpécialitésUseCase {
  public constructor(private readonly _bacRepository: BacRepository) {}

  public async run(spécialitéIds?: Array<Spécialité["id"]>): Promise<Spécialité[] | undefined> {
    return await this._bacRepository.récupérerSpécialités(spécialitéIds);
  }
}
