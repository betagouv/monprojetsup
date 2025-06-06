import { Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { type TraceService } from "@/services/trace/trace.interface";

export class RécupérerFormationsUseCase {
  public constructor(
    private readonly _formationRepository: FormationRepository,
    private readonly _traceService: TraceService,
  ) {}

  public async run(formationIds: Array<Formation["id"]>): Promise<Formation[] | Error> {
    void this._traceService.ajouterTraceFicheFormation(formationIds.join(" "));
    return await this._formationRepository.récupérerPlusieurs(formationIds);
  }
}
