import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import { type TraceService } from "@/services/trace/trace.interface";

export class RécupérerMétierUseCase {
  public constructor(
    private readonly _métierRepository: MétierRepository,
    private readonly _traceService: TraceService,
  ) {}

  public async run(métierId: Métier["id"]): Promise<Métier | Error> {
    void this._traceService.ajouterTraceFicheMetier(métierId);
    return await this._métierRepository.récupérer(métierId);
  }
}
