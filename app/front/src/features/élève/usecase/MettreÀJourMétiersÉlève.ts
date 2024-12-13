import { AnalyticsRepository } from "@/features/analytics/infrastructure/analytics.interface";
import { type Élève, MétierÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourMétiersÉlèveUseCase {
  public constructor(
    private readonly _élèveRepository: ÉlèveRepository,
    private readonly _analytics: AnalyticsRepository,
  ) {}

  public async run(élève: Élève, idsMétiersÀModifier: MétierÉlève[]): Promise<Élève | Error> {
    const métiers = new Set(élève.métiersFavoris);

    for (const idMétier of idsMétiersÀModifier) {
      if (métiers.has(idMétier)) {
        métiers.delete(idMétier);
        this._analytics.envoyerÉvènement("Métier Favori", "Supprimer", idMétier);
      } else {
        métiers.add(idMétier);
        this._analytics.envoyerÉvènement("Métier Favori", "Ajouter", idMétier);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, métiersFavoris: [...métiers] });
  }
}
