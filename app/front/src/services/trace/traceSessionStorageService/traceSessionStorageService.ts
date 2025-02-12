import { type TraceService } from "@/services/trace/trace.interface";

export class TraceSessionStorageService implements TraceService {
  public ajouterTraceFicheFormation(): Promise<void> {
    return Promise.resolve();
  }

  public async ajouterTraceFicheMetier(): Promise<void> {}

  public async ajouterTraceRechercheFormation(): Promise<void> {}

  public async ajouterTraceSuggestions(): Promise<void> {}

  public async ajouterTraceOngletFicheFormation(): Promise<void> {}

  public async ajouterTraceLienExterne(): Promise<void> {}
}
