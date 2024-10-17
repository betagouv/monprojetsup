import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class RécupérerÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(): Promise<Élève | undefined> {
    const élève = await this._élèveRepository.récupérerProfil();

    if (!élève) {
      await this._élèveRepository.mettreÀJourProfil({
        compteParcoursupAssocié: false,
        situation: null,
        classe: null,
        bac: null,
        spécialités: null,
        domaines: null,
        centresIntérêts: null,
        métiersFavoris: null,
        duréeÉtudesPrévue: null,
        alternance: null,
        moyenneGénérale: null,
        communesFavorites: null,
        formationsFavorites: null,
        formationsMasquées: null,
      });

      return await this._élèveRepository.récupérerProfil();
    }

    return élève;
  }
}
