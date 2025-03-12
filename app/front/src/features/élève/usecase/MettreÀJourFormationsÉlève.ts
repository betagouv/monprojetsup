import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { Formation } from "@/features/formation/domain/formation.interface.ts";
import { FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { AnalyticsRepository } from "@/services/analytics/analytics.interface";

export class MettreÀJourFormationsÉlèveUseCase {
  public constructor(
    private readonly _élèveRepository: ÉlèveRepository,
    private readonly _formationRepository: FormationRepository,
    private readonly _analytics: AnalyticsRepository,
  ) {}

  public async run(élève: Élève, idsFormationsÀModifier: Formation["id"][]): Promise<Élève | Error> {
    const formations = new Set(élève.formations);

    const réponse = await this._formationRepository.récupérerPlusieursFiches(idsFormationsÀModifier);
    if (réponse instanceof Error) {
      return réponse;
    }

    for (const fiche of réponse) {
      const idFormation = fiche.id;
      const label = fiche.nom + " (" + fiche.id + ")";
      if (formations.has(idFormation)) {
        formations.delete(idFormation);
        this._analytics.envoyerÉvènement("Formation Favorite", "Supprimer", label);
      } else {
        formations.add(idFormation);
        this._analytics.envoyerÉvènement("Formation Favorite", "Ajouter", label);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, formations: [...formations] });
  }
}
