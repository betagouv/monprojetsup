import { type CommuneHttp, type RechercherCommunesRéponseHttp } from "./communeHttpRepository.interface";
import { type Commune } from "@/features/commune/domain/commune.interface";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";
import { type HttpClient } from "@/services/httpClient/httpClient";

export class communeHttpRepository implements CommuneRepository {
  public constructor(private _httpClient: HttpClient) {}

  public async rechercher(recherche: string): Promise<Commune[] | undefined> {
    const réponse = await this._httpClient.récupérer<RechercherCommunesRéponseHttp>({
      endpoint: `https://api-adresse.data.gouv.fr/search/?q=${recherche}&limit=20&type=municipality`,
      méthode: "GET",
    });

    if (!réponse) return undefined;

    return réponse.features.map((commune) => this._mapperVersDomaine(commune));
  }

  private _mapperVersDomaine(commune: CommuneHttp): Commune {
    return {
      codeInsee: commune.properties.citycode,
      codePostal: commune.properties.postcode,
      nom: commune.properties.name,
      latitude: commune.geometry.coordinates[1],
      longitude: commune.geometry.coordinates[0],
    };
  }
}
