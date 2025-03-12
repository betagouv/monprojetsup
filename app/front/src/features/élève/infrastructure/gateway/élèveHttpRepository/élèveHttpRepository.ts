import {
  AssocierCompteParcourSupÉlèveRéponseHTTP,
  type BodyMettreÀJourProfilÉlèveHTTP,
  type MettreÀJourProfilÉlèveRéponseHTTP,
  type RécupérerProfilÉlèveRéponseHTTP,
  type RécupérerProgressionÉlèveRéponseHTTP,
} from "./élèveHttpRepository.interface";
import { type Élève, ProgressionÉlève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { ÉlèveSessionStorageRepository } from "@/features/élève/infrastructure/gateway/élèveSessionStorageRepository/élèveSessionStorageRepository";
import { RessourceNonTrouvéeErreurHttp } from "@/services/erreurs/erreursHttp";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

export class ÉlèveHttpRepository implements ÉlèveRepository {
  private readonly _ENDPOINT_PROFIL = "/api/v1/auth/profil";

  private readonly _ENDPOINT_PROGRESSION = "/api/v1/auth/profil/progression";

  private readonly _storageRepository = new ÉlèveSessionStorageRepository();

  private readonly profilVierge = {
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
    communesFavorites: null,
    formations: null,
    voeuxFavoris: null,
    formationsMasquées: null,
    notesPersonnelles: null,
    ambitions: null,
  };

  public constructor(private readonly _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérerProfil(): Promise<Élève | Error> {
    if (!this._mpsApiHttpClient.estAuthentifié()) {
      return this._storageRepository.récupérerProfil();
    }

    const réponse = await this._mpsApiHttpClient.get<RécupérerProfilÉlèveRéponseHTTP>(this._ENDPOINT_PROFIL);

    if (réponse instanceof RessourceNonTrouvéeErreurHttp) {
      const profilLocal = await this._storageRepository.récupérerProfil();
      const profilAUtiliser = profilLocal instanceof Error ? this.profilVierge : profilLocal;
      profilAUtiliser.compteParcoursupAssocié = false;
      await this.mettreÀJourProfil(profilAUtiliser);
      const profilDistant = await this.récupérerProfil();
      await this.effacerProfilLocal();
      return profilDistant;
    }

    if (réponse instanceof Error) {
      return réponse;
    }

    await this.effacerProfilLocal();

    return this._mapperVersLeDomaine(réponse);
  }

  public récupérerProfilLocal(): Élève | null {
    if (this._mpsApiHttpClient.estAuthentifié()) return null;
    const profilLocal = this._storageRepository.récupérerProfilLocal();
    if (profilLocal === this.profilVierge) return null;
    return profilLocal;
  }

  private async effacerProfilLocal() {
    await this._storageRepository.mettreÀJourProfil(this.profilVierge);
  }

  public async récupérerProgressionÉlève(): Promise<ProgressionÉlève> {
    if (!this._mpsApiHttpClient.estAuthentifié()) {
      return this._storageRepository.récupérerProgressionÉlève();
    }

    const réponse = await this._mpsApiHttpClient.get<RécupérerProgressionÉlèveRéponseHTTP>(this._ENDPOINT_PROGRESSION);

    if (réponse instanceof RessourceNonTrouvéeErreurHttp || réponse instanceof Error) {
      return 0;
    } else {
      return this._mapperProgressionVersLeDomaine(réponse);
    }
  }

  public async mettreÀJourProfil(élève: Élève): Promise<Élève | Error> {
    if (!this._mpsApiHttpClient.estAuthentifié()) {
      return this._storageRepository.mettreÀJourProfil(élève);
    }

    const réponse = await this._mpsApiHttpClient.post<MettreÀJourProfilÉlèveRéponseHTTP>(
      this._ENDPOINT_PROFIL,
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
    if (!this._mpsApiHttpClient.estAuthentifié()) {
      throw new Error("Non-authentifié");
    }

    const réponse = await this._mpsApiHttpClient.post<AssocierCompteParcourSupÉlèveRéponseHTTP>(
      `${this._ENDPOINT_PROFIL}/parcoursup`,
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

  private _mapperProgressionVersLeDomaine(progression: RécupérerProgressionÉlèveRéponseHTTP): ProgressionÉlève {
    return progression.progression;
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
