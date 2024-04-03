import { type Élève } from "@/features/élève/domain/élève.interface";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";

export class élèveSessionStorageRepository implements ÉlèveRepository {
  private _SESSION_STORAGE_PREFIX = "élève";

  public async récupérer(): Promise<Élève | undefined> {
    const élève = sessionStorage.getItem(this._SESSION_STORAGE_PREFIX);

    if (!élève) return undefined;

    return JSON.parse(élève);
  }

  public async mettreÀJour(données: Partial<Omit<Élève, "id">>): Promise<Élève | undefined> {
    const élève = await this.récupérer();

    if (!élève) return undefined;

    const élèveMisÀJour: Élève = { ...élève, ...données };

    sessionStorage.setItem(this._SESSION_STORAGE_PREFIX, JSON.stringify(élèveMisÀJour));

    return élèveMisÀJour;
  }

  public async créer(): Promise<Élève> {
    const id = Date.now().toString();
    const élève = { id };

    sessionStorage.setItem(this._SESSION_STORAGE_PREFIX, JSON.stringify(élève));
    return élève;
  }
}
