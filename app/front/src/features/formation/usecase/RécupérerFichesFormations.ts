import { type FicheFormation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RécupérerFichesFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(formationIds: Array<FicheFormation["id"]>): Promise<FicheFormation[] | Error> {
    return await this._formationRepository.récupérerPlusieursFiches(formationIds);
  }
}
