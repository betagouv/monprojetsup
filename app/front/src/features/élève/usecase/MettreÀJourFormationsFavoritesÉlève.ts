import { Formation } from "@/features/formation/domain/formation.interface.ts";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourFormationsFavoritesÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, idsFormationsÀModifier: Formation["id"][]): Promise<Élève | Error> {
    const formations = new Map(élève.formationsFavorites?.map((formation) => [formation.id, formation]));

    for (const idFormation of idsFormationsÀModifier) {
      if (formations.has(idFormation)) {
        formations.delete(idFormation);
      } else {
        formations.set(idFormation, {
          id: idFormation,
          niveauAmbition: null,
          commentaire: null,
        });
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({ ...élève, formationsFavorites: [...formations.values()] });
  }
}
