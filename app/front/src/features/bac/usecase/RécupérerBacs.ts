import { type Bac } from "@/features/bac/domain/bac.interface";
import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";

export class RécupérerBacsUseCase {
  public constructor(private readonly _bacRepository: BacRepository) {}

  public async run(): Promise<Bac[] | undefined> {
    return await this._bacRepository.récupérerTous();
  }
}
