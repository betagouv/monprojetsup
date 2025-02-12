
import { type TraceService } from "@/services/trace/trace.interface";

export class TraceSessionStorageService implements TraceService {

  public ajouterTraceFicheFormation(id: string): Promise<void> {
    return Promise.resolve();
  }

  public async ajouterTraceFicheMetier(id: string): Promise<void> {
    return Promise.resolve();
  }

  public async ajouterTraceRechercheFormation(mots: string): Promise<void> {
    return Promise.resolve();
  };

  public async ajouterTraceSuggestions (): Promise<void> {
    return Promise.resolve();
  };

  public async ajouterTraceOngletFicheFormation(id: string, idOnglet: string): Promise<void> {
    return Promise.resolve();
  };

  public async ajouterTraceLienExterne(id: string, url: string): Promise<void> {
    return Promise.resolve();
  };

}
