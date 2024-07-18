import {
  type MettreÀJourProfilÉlèveRéponseHTTP,
  type RécupérerProfilÉlèveRéponseHTTP,
} from "./élèveHttpRepository.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

export class ÉlèveHttpRepository implements ÉlèveRepository {
  private _ENDPOINT = "/api/v1/profil" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérerProfil(): Promise<Élève | undefined> {
    const réponse = await this._mpsApiHttpClient.get<RécupérerProfilÉlèveRéponseHTTP>(this._ENDPOINT);

    if (!réponse) return undefined;

    return this._mapperVersLeDomaine(réponse);
  }

  public async mettreÀJourProfil(élève: Élève): Promise<Élève | undefined> {
    const réponse = await this._mpsApiHttpClient.post<MettreÀJourProfilÉlèveRéponseHTTP>(this._ENDPOINT, élève);

    if (!réponse) return undefined;

    return élève;
  }

  private _mapperVersLeDomaine(élève: RécupérerProfilÉlèveRéponseHTTP): Élève {
    return {
      situation: élève.situation ?? null,
      classe: élève.classe ?? null,
      bac: élève.baccalaureat ?? null,
      spécialités: élève.specialites ?? null,
      domaines: élève.domaines ?? null,
      centresIntêrets: élève.centresInterets ?? null,
      métiersFavoris: élève.metiersFavoris ?? null,
      duréeÉtudesPrévue: élève.dureeEtudesPrevue ?? null,
      alternance: élève.alternance ?? null,
      communesFavorites: élève.communesFavorites ?? null,
      formationsFavorites: élève.formationsFavorites ?? null,
    };
  }
}
