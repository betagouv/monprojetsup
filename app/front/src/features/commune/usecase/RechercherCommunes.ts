import { type Commune } from "@/features/commune/domain/commune.interface";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";

export class RechercherCommunesUseCase {
  public constructor(private readonly _communeRepository: CommuneRepository) {}

  public async run(recherche: string): Promise<Commune[] | undefined> {
    const communes = await this._communeRepository.rechercher(recherche);

    if (!communes) return undefined;

    const communesAvecGestionHomonymes = communes.slice(0, 20).map((commune, index) => {
      const communeExistanteAvecLeMêmeNom = communes.some((subCommune, subIndex) => {
        return subCommune.nom.toLocaleLowerCase() === commune.nom.toLocaleLowerCase() && subIndex !== index;
      });

      if (communeExistanteAvecLeMêmeNom) return { ...commune, nom: `${commune.nom} (${commune.codePostal})` };

      return commune;
    });

    return communesAvecGestionHomonymes;
  }
}
