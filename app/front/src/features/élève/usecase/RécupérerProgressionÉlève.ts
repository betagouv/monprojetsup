import { ProgressionÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class RécupérerProgressionÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(): Promise<ProgressionÉlève> {
    return await this._élèveRepository.récupérerProgressionÉlève();
  }
}
