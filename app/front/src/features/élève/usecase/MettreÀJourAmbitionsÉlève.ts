import { AmbitionFormationÉlève, type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class MettreÀJourAmbitionsÉlèveUseCase {
  public constructor(private readonly _élèveRepository: ÉlèveRepository) {}

  public async run(élève: Élève, ambitionsÀModifier: AmbitionFormationÉlève[]): Promise<Élève | Error> {
    const ambitions = new Map(élève.ambitions?.map((ambition) => [ambition.idFormation, ambition]));

    for (const ambition of ambitionsÀModifier) {
      if (ambitions.has(ambition.idFormation) && ambition.ambition === null) {
        ambitions.delete(ambition.idFormation);
      } else {
        ambitions.set(ambition.idFormation, ambition);
      }
    }

    return await this._élèveRepository.mettreÀJourProfil({
      ...élève,
      ambitions: [...ambitions.values()],
    });
  }
}
