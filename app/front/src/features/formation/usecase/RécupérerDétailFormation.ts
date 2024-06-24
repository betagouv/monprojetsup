import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RécupérerDétailFormationUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(formationId: Formation["id"]): Promise<Formation | undefined> {
    return await this._formationRepository.récupérer(formationId);
  }
}
