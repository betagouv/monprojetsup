import { type TraceService } from "@/services/trace/trace.interface";

export class VoirMétierUseCase {
  public constructor(
    private readonly _traceService: TraceService
  ) {}

  public async run(métierId : string): Promise<void | Error> {
    return this._traceService.ajouterTraceFicheMetier(métierId);
  }

}
