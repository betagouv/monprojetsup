/* eslint-disable @typescript-eslint/require-await */
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class ÉlèveSessionStorageRepository implements ÉlèveRepository {
  private _SESSION_STORAGE_PREFIX = "élève";

  private _élève: Élève = {
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
    formationsFavorites: null,
    formationsMasquées: null,
  };

  public async récupérerProfil(): Promise<Élève | undefined> {
    const élève = sessionStorage.getItem(this._SESSION_STORAGE_PREFIX);

    if (élève) {
      this._élève = JSON.parse(élève) as Élève;
    } else {
      sessionStorage.setItem(this._SESSION_STORAGE_PREFIX, JSON.stringify(this._élève));
    }

    return this._élève;
  }

  public async mettreÀJourProfil(élève: Élève): Promise<Élève | undefined> {
    this._élève = élève;
    sessionStorage.setItem(this._SESSION_STORAGE_PREFIX, JSON.stringify(this._élève));

    return this._élève;
  }

  public async associerCompteParcourSup(): Promise<boolean | undefined> {
    const élève = await this.récupérerProfil();

    if (élève) {
      await this.mettreÀJourProfil({ ...élève, compteParcoursupAssocié: true });
      return true;
    }

    return false;
  }
}
