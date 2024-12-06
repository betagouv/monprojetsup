import {
  AssocierCompteParcourSupÉlèveRéponseHTTP,
  type BodyMettreÀJourProfilÉlèveHTTP,
  type MettreÀJourProfilÉlèveRéponseHTTP,
  type RécupérerProfilÉlèveRéponseHTTP,
} from "./élèveHttpRepository.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { RessourceNonTrouvéeErreurHttp } from "@/services/erreurs/erreursHttp";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

export class ÉlèveHttpRepository implements ÉlèveRepository {
  private _ENDPOINT = "/api/v1/profil" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérerProfil(): Promise<Élève | Error> {
    const réponse = await this._mpsApiHttpClient.get<RécupérerProfilÉlèveRéponseHTTP>(this._ENDPOINT);

    if (réponse instanceof RessourceNonTrouvéeErreurHttp) {
      await this.mettreÀJourProfil({
        compteParcoursupAssocié: false,
        situation: null,
        classe: null,
        bac: null,
        spécialités: null,
        domaines: null,
        centresIntérêts: null,
        métiersFavoris: null,
        duréeÉtudesPrévue: null,
        alternance: null,
        moyenneGénérale: null,
        communesFavorites: null,
        formations: null,
        voeuxFavoris: null,
        formationsMasquées: null,
        notesPersonnelles: null,
        ambitions: null,
      });

      return await this.récupérerProfil();
    }

    if (réponse instanceof Error) {
      return réponse;
    }

    return this._mapperVersLeDomaine(réponse);
  }

  public async mettreÀJourProfil(élève: Élève): Promise<Élève | Error> {
    const réponse = await this._mpsApiHttpClient.post<MettreÀJourProfilÉlèveRéponseHTTP>(
      this._ENDPOINT,
      this._mapperVersLApiMps(élève),
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return élève;
  }

  public async associerCompteParcourSup(
    codeVerifier: string,
    code: string,
    redirectUri: string,
  ): Promise<boolean | Error> {
    const réponse = await this._mpsApiHttpClient.post<AssocierCompteParcourSupÉlèveRéponseHTTP>(
      `${this._ENDPOINT}/parcoursup`,
      {
        codeVerifier,
        code,
        redirectUri,
      },
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return true;
  }

  private _mapperVersLApiMps(élève: Élève): BodyMettreÀJourProfilÉlèveHTTP {
    return {
      situation: élève.situation ?? undefined,
      classe: élève.classe ?? undefined,
      baccalaureat: élève.bac ?? undefined,
      specialites: élève.spécialités ?? undefined,
      domaines: élève.domaines ?? undefined,
      centresInterets: élève.centresIntérêts ?? undefined,
      metiersFavoris: élève.métiersFavoris ?? undefined,
      dureeEtudesPrevue: élève.duréeÉtudesPrévue ?? undefined,
      alternance: élève.alternance ?? undefined,
      moyenneGenerale: élève.moyenneGénérale ?? undefined,
      communesFavorites: élève.communesFavorites ?? undefined,
      corbeilleFormations: élève.formationsMasquées ?? undefined,
      formationsFavorites:
        élève.formations?.map((idFormation) => ({
          idFormation,
          niveauAmbition: élève.ambitions?.find((ambition) => ambition.idFormation === idFormation)?.ambition ?? 0,
          priseDeNote: élève.notesPersonnelles?.find((note) => note.idFormation === idFormation)?.note ?? undefined,
        })) ?? undefined,
      voeuxFavoris:
        élève.voeuxFavoris?.map((voeuFavori) => ({
          idVoeu: voeuFavori.id,
          estFavoriParcoursup: voeuFavori.estParcoursup,
        })) ?? undefined,
    };
  }

  private _mapperVersLeDomaine(élève: RécupérerProfilÉlèveRéponseHTTP): Élève {
    return {
      compteParcoursupAssocié: élève.compteParcoursupAssocie ?? false,
      situation: élève.situation ?? null,
      classe: élève.classe ?? null,
      bac: élève.baccalaureat ?? null,
      spécialités: élève.specialites ?? null,
      domaines: élève.domaines ?? null,
      centresIntérêts: élève.centresInterets ?? null,
      métiersFavoris: élève.metiersFavoris ?? null,
      duréeÉtudesPrévue: élève.dureeEtudesPrevue ?? null,
      alternance: élève.alternance ?? null,
      moyenneGénérale: élève.moyenneGenerale ?? null,
      communesFavorites: élève.communesFavorites ?? null,
      formationsMasquées: élève.corbeilleFormations ?? null,
      formations: élève.formationsFavorites?.map(({ idFormation }) => idFormation) ?? null,
      voeuxFavoris:
        élève.voeuxFavoris?.map((voeu) => ({
          id: voeu.idVoeu,
          estParcoursup: voeu.estFavoriParcoursup,
        })) ?? null,
      notesPersonnelles:
        élève.formationsFavorites?.map((formationFavorite) => ({
          idFormation: formationFavorite.idFormation,
          note: formationFavorite.priseDeNote ?? null,
        })) ?? null,
      ambitions:
        élève.formationsFavorites?.map((formationFavorite) => ({
          idFormation: formationFavorite.idFormation,
          ambition: [1, 2, 3].includes(formationFavorite.niveauAmbition)
            ? (formationFavorite.niveauAmbition as 1 | 2 | 3)
            : null,
        })) ?? null,
    };
  }
}
