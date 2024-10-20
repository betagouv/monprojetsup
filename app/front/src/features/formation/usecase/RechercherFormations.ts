import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RechercherFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(recherche: string): Promise<Formation[] | undefined> {
    return await this._formationRepository.rechercher(recherche);
  }
}
