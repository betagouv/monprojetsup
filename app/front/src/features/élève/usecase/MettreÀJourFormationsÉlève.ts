import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { Formation } from "@/features/formation/domain/formation.interface.ts";
import { AnalyticsRepository } from "@/services/analytics/analytics.interface";

export class MettreÀJourFormationsÉlèveUseCase {
  public constructor(
    private readonly _élèveRepository: ÉlèveRepository,
    private readonly _analytics: AnalyticsRepository,
  ) {}

  public async run(élève: Élève, idsFormationsÀModifier: Formation["id"][]): Promise<Élève | Error> {
    const formations = new Set(élève.formations);

    for (const idFormation of idsFormationsÀModifier) {
      if (formations.has(idFormation)) {
        formations.delete(idFormation);
        this._analytics.envoyerÉvènement("Formation Favorite", "Supprimer", idFormation);
      } else {
        formations.add(idFormation);
        this._analytics.envoyerÉvènement("Formation Favorite", "Ajouter", idFormation);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, formations: [...formations] });
  }
}
