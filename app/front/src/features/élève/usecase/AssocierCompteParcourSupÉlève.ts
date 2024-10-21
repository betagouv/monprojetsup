import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class AssocierCompteParcourSupÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(codeVerifier: string, code: string, redirectUri: string): Promise<boolean> {
    const réponse = await this._élèveRepository.associerCompteParcourSup(codeVerifier, code, redirectUri);

    if (!réponse) return false;

    return réponse;
  }
}
