import { type Commune } from "@/features/commune/domain/commune.interface";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";

export class RechercherCommunesUseCase {
  public constructor(private readonly _communeRepository: CommuneRepository) {}

  public async run(recherche: string): Promise<Commune[] | undefined> {
    const communes = await this._communeRepository.rechercher(recherche);
    return communes?.slice(0, 20);
  }
}
