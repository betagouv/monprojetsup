import { VoeuÉlève, type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourVoeuxÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, idsVoeuxÀModifier: VoeuÉlève["id"][]): Promise<Élève | Error> {
    const voeux = new Map(élève.voeuxFavoris?.map((voeu) => [voeu.id, voeu]));

    for (const idVoeu of idsVoeuxÀModifier) {
      if (voeux.has(idVoeu)) {
        voeux.delete(idVoeu);
      } else {
        voeux.set(idVoeu, { id: idVoeu, estParcoursup: false });
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, voeuxFavoris: [...voeux.values()] });
  }
}
