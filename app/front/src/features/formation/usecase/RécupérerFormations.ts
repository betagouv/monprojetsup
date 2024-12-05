import { Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RécupérerFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(formationIds: Array<Formation["id"]>): Promise<Formation[] | Error> {
    return await this._formationRepository.récupérerPlusieurs(formationIds);
  }
}
