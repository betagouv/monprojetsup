import { type Élève, MétierÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import { AnalyticsRepository } from "@/services/analytics/analytics.interface";

export class MettreÀJourMétiersÉlèveUseCase {
  public constructor(
    private readonly _élèveRepository: ÉlèveRepository,
    private readonly _métierRepository: MétierRepository,
    private readonly _analytics: AnalyticsRepository,
  ) {}

  public async run(élève: Élève, idsMétiersÀModifier: MétierÉlève[]): Promise<Élève | Error> {
    const métiers = new Set(élève.métiersFavoris);

    const réponse = await this._métierRepository.récupérerPlusieurs(idsMétiersÀModifier);
    if (réponse instanceof Error) {
      return réponse;
    }

    for (const métier of réponse) {
      const idMétier = métier.id;
      const label = métier.nom + " (" + idMétier + ")";
      if (métiers.has(idMétier)) {
        métiers.delete(idMétier);
        this._analytics.envoyerÉvènement("Métier Favori", "Supprimer", label);
      } else {
        métiers.add(idMétier);
        this._analytics.envoyerÉvènement("Métier Favori", "Ajouter", label);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, métiersFavoris: [...métiers] });
  }
}
