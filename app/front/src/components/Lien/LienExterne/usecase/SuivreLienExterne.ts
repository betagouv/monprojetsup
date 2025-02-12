import { type TraceService } from "@/services/trace/trace.interface";

export class SuivreLienExterneUseCase {
  public constructor(
    private readonly _traceService: TraceService
  ) {}

  public async run(id: string, url : string): Promise<void | Error> {
    return this._traceService.ajouterTraceLienExterne(id, url);
  }

}
