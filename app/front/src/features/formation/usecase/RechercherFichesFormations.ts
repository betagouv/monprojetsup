import { type FicheFormation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class RechercherFichesFormationsUseCase {
  public constructor(private readonly _formationRepository: FormationRepository) {}

  public async run(recherche: string): Promise<FicheFormation[] | Error> {
    return await this._formationRepository.rechercherFichesFormations(recherche);
  }
}
