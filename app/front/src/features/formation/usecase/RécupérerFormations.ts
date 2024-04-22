import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RécupérerFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(): Promise<Formation[] | undefined> {
    return await this._formationRepository.récupérerToutes();
  }
}
