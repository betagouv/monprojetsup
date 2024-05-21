import { type Ville } from "@/features/ville/domain/ville.interface";
import { type VilleRepository } from "@/features/ville/infrastructure/villeRepository.interface";

export class RechercherVillesUseCase {
  public constructor(private readonly _villeRepository: VilleRepository) {}

  public async run(recherche: string): Promise<Ville[] | undefined> {
    const villes = await this._villeRepository.rechercher(recherche);
    return villes?.slice(0, 20);
  }
}
