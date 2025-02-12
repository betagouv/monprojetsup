import { type TraceService } from "@/services/trace/trace.interface";

export class VoirOngletFormationUseCase {
  public constructor(private readonly _traceService: TraceService) {}

  public async run(formationId: string, tabId: string): Promise<void | Error> {
    return this._traceService.ajouterTraceOngletFicheFormation(formationId, tabId);
  }
}
