import { type Élève, FormationMasquéeÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourFormationsMasquéesÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, idsFormationsMasquéesÀModifier: FormationMasquéeÉlève[]): Promise<Élève | Error> {
    const formationsMasquées = new Set(élève.formationsMasquées);

    for (const idFormationsMasquée of idsFormationsMasquéesÀModifier) {
      if (formationsMasquées.has(idFormationsMasquée)) {
        formationsMasquées.delete(idFormationsMasquée);
      } else {
        formationsMasquées.add(idFormationsMasquée);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({
      ...élève,
      formationsMasquées: [...formationsMasquées],
    });
  }
}
