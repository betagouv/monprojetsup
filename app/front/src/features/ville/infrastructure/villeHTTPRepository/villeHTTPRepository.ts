import { type RechercherVillesRéponseHTTP, type VilleHTTP } from "./villeHTTPRepository.interface";
import { type Ville } from "@/features/ville/domain/ville.interface";
import { type VilleRepository } from "@/features/ville/infrastructure/villeRepository.interface";
import { type HttpClient } from "@/services/httpClient/httpClient";

export class villeHTTPRepository implements VilleRepository {
  public constructor(private _httpClient: HttpClient) {}

  public async rechercher(recherche: string): Promise<Ville[] | undefined> {
    const réponse = await this._httpClient.fetch<RechercherVillesRéponseHTTP>({
      endpoint: `https://api-adresse.data.gouv.fr/search/?q=${recherche}&limit=20&type=municipality`,
      method: "GET",
    });

    if (!réponse) return undefined;

    return réponse.features.map(this._mapperVersDomaine);
  }

  private _mapperVersDomaine(ville: VilleHTTP): Ville {
    return {
      codeInsee: ville.properties.citycode,
      nom: ville.properties.name,
      latitude: ville.geometry.coordinates[1],
      longitude: ville.geometry.coordinates[0],
    };
  }
}
