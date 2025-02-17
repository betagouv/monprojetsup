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
      Pick<Élève, "situation" | "classe" | "bac" | "duréeÉtudesPrévue" | "alternance" >
    >,
  ): Promise<Élève | Error> {
    this._analytics.envoyerÉvènement("Profil", "Mise à jour", "");

    const élève = {
      ...profilÉlève,
      ...changementsProfilÉlève,
    };

    if (changementsProfilÉlève.bac !== undefined && changementsProfilÉlève.bac !== profilÉlève.bac) {
      élève.spécialités = [];
    }

    return await this._élèveRepository.mettreÀJourProfil(élève);
  }
}
