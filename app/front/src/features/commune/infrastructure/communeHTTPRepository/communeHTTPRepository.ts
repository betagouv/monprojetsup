import { type CommuneHTTP, type RechercherCommunesRéponseHTTP } from "./communeHTTPRepository.interface";
import { type Commune } from "@/features/commune/domain/commune.interface";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";
import { type HttpClient } from "@/services/httpClient/httpClient";

export class communeHTTPRepository implements CommuneRepository {
  public constructor(private _httpClient: HttpClient) {}

  public async rechercher(recherche: string): Promise<Commune[] | undefined> {
    const réponse = await this._httpClient.récupérer<RechercherCommunesRéponseHTTP>({
      endpoint: `https://api-adresse.data.gouv.fr/search/?q=${recherche}&limit=20&type=municipality`,
      méthode: "GET",
    });

    if (!réponse) return undefined;

    return réponse.features.map(this._mapperVersDomaine);
  }

  private _mapperVersDomaine(commune: CommuneHTTP): Commune {
    return {
      codeInsee: commune.properties.citycode,
      nom: commune.properties.name,
      latitude: commune.geometry.coordinates[1],
      longitude: commune.geometry.coordinates[0],
    };
  }
}
