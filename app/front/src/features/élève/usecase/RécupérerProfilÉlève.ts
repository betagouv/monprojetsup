import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class RécupérerÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(): Promise<Élève | Error> {
    return await this._élèveRepository.récupérerProfil();
  }
}

export class RécupérerProfilLocalUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public run(): Élève | null {
    return this._élèveRepository.récupérerProfilLocal();
  }
}
