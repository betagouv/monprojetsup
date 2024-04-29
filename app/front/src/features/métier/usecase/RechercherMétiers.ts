import { type MétierAperçu } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class RechercherMétiersUseCase {
  public constructor(private readonly _métierRepository: MétierRepository) {}

  public async run(recherche: string): Promise<MétierAperçu[] | undefined> {
    if (recherche.length < 3) return [];

    const métiers = await this._métierRepository.rechercher(recherche);
    return métiers?.slice(0, 20);
  }
}
