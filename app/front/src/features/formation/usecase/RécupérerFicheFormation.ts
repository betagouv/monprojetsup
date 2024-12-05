import { type FicheFormation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RécupérerFicheFormationUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(formationId: FicheFormation["id"]): Promise<FicheFormation | Error> {
    return await this._formationRepository.récupérerUneFiche(formationId);
  }
}
