import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourProfilÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(
    profilÉlève: Élève,
    changementsProfilÉlève: Partial<
      Pick<Élève, "situation" | "classe" | "bac" | "duréeÉtudesPrévue" | "alternance" | "moyenneGénérale">
    >,
  ): Promise<Élève | Error> {
    return await this._élèveRepository.mettreÀJourProfil({ ...profilÉlève, ...changementsProfilÉlève });
  }
}
