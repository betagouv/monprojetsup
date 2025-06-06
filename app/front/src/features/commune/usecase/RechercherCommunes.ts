import { type Commune } from "@/features/commune/domain/commune.interface";
import { type CommuneRepository } from "@/features/commune/infrastructure/gateway/communeRepository.interface";

export class RechercherCommunesUseCase {
  public constructor(private readonly _communeRepository: CommuneRepository) {}

  public async run(recherche: string): Promise<Commune[] | Error> {
    const réponse = await this._communeRepository.rechercher(recherche);

    if (réponse instanceof Error) return réponse;

    const communesAvecGestionHomonymes = réponse.slice(0, 20).map((commune, index) => {
      const communeExistanteAvecLeMêmeNom = réponse.some((subCommune, subIndex) => {
        return subCommune.nom.toLocaleLowerCase() === commune.nom.toLocaleLowerCase() && subIndex !== index;
      });

      if (communeExistanteAvecLeMêmeNom) return { ...commune, nom: `${commune.nom} (${commune.codePostal})` };

      return commune;
    });

    return communesAvecGestionHomonymes;
  }
}
