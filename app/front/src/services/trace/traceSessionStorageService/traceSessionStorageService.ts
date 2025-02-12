import { type TraceService } from "@/services/trace/trace.interface";

export class TraceSessionStorageService implements TraceService {
  public ajouterTraceFicheFormation(): Promise<void> {
    return Promise.resolve();
  }

  public async ajouterTraceFicheMetier() {}

  public async ajouterTraceRechercheFormation() {}

  public async ajouterTraceSuggestions() {}

  public async ajouterTraceOngletFicheFormation() {}

  public async ajouterTraceLienExterne() {}
}
