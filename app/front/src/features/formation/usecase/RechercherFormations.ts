import { type FormationAperçu } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RechercherFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(recherche: string): Promise<FormationAperçu[] | undefined> {
    const formations = await this._formationRepository.rechercher(recherche);
    return formations?.slice(0, 20);
  }
}
