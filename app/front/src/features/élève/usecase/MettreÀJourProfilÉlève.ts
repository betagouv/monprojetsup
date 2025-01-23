import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { AnalyticsRepository } from "@/services/analytics/analytics.interface";

export class MettreÀJourProfilÉlèveUseCase {
  public constructor(
    private readonly _élèveRepository: ÉlèveRepository,
    private readonly _analytics: AnalyticsRepository,
  ) {}

  public async run(
    profilÉlève: Élève,
    changementsProfilÉlève: Partial<
      Pick<Élève, "situation" | "classe" | "bac" | "duréeÉtudesPrévue" | "alternance" | "moyenneGénérale">
    >,
  ): Promise<Élève | Error> {
    this._analytics.envoyerÉvènement("Profil", "Mise à jour", "");

    return await this._élèveRepository.mettreÀJourProfil({
      ...profilÉlève,
      ...changementsProfilÉlève,
      spécialités: changementsProfilÉlève.bac === profilÉlève.bac ? profilÉlève.spécialités : [],
    });
  }
}
