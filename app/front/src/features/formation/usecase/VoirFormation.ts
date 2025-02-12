import { type TraceService } from "@/services/trace/trace.interface";

export class VoirFormationUseCase {
  public constructor(
    private readonly _traceService: TraceService
  ) {}

  public async run(formationId : string): Promise<void | Error> {
    return this._traceService.ajouterTraceFicheFormation(formationId);
  }

}
