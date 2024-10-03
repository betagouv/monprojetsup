import {
  type BodyMettreÀJourProfilÉlèveHTTP,
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
    const réponse = await this._mpsApiHttpClient.post<MettreÀJourProfilÉlèveRéponseHTTP>(
      this._ENDPOINT,
      this._mapperVersLApiMps(élève),
    );

    if (!réponse) return undefined;

    return élève;
  }

  private _mapperVersLApiMps(élève: Élève): BodyMettreÀJourProfilÉlèveHTTP {
    return {
      situation: élève.situation ?? undefined,
      classe: élève.classe ?? undefined,
      baccalaureat: élève.bac ?? undefined,
      specialites: élève.spécialités ?? undefined,
      domaines: élève.domaines ?? undefined,
      centresInterets: élève.centresIntêrets ?? undefined,
      metiersFavoris: élève.métiersFavoris ?? undefined,
      dureeEtudesPrevue: élève.duréeÉtudesPrévue ?? undefined,
      alternance: élève.alternance ?? undefined,
      moyenneGenerale: élève.moyenneGénérale ?? undefined,
      communesFavorites: élève.communesFavorites ?? undefined,
      corbeilleFormations: élève.formationsMasquées ?? undefined,
      formationsFavorites:
        élève.formationsFavorites?.map((formationFavorite) => ({
          idFormation: formationFavorite.id,
          niveauAmbition: formationFavorite.niveauAmbition ?? 0,
          voeuxChoisis: formationFavorite.voeux ?? [],
          priseDeNote: formationFavorite.commentaire ?? undefined,
        })) ?? undefined,
    };
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
      moyenneGénérale: élève.moyenneGenerale ?? null,
      communesFavorites: élève.communesFavorites ?? null,
      formationsMasquées: élève.corbeilleFormations ?? null,
      formationsFavorites:
        élève.formationsFavorites?.map((formationFavorite) => ({
          id: formationFavorite.idFormation,
          niveauAmbition: [1, 2, 3].includes(formationFavorite.niveauAmbition)
            ? (formationFavorite.niveauAmbition as 1 | 2 | 3)
            : null,
          voeux: formationFavorite.voeuxChoisis,
          commentaire: formationFavorite.priseDeNote ?? null,
        })) ?? null,
    };
  }
}
