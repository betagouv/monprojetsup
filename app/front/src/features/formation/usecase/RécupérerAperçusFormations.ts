import { type FormationAperçu } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RécupérerAperçusFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(formationIds: Array<FormationAperçu["id"]>): Promise<FormationAperçu[] | undefined> {
    return await this._formationRepository.récupérerAperçus(formationIds);
  }
}
