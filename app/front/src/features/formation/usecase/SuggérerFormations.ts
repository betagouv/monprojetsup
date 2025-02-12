import { type FicheFormation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { type TraceService } from "@/services/trace/trace.interface";

export class SuggérerFormationsUseCase {
  public constructor(
    private readonly _formationRepository: FormationRepository,
    private readonly _traceService: TraceService,
  ) {}

  public async run(): Promise<FicheFormation[] | Error> {
    this._traceService.ajouterTraceSuggestions()
    return await this._formationRepository.suggérer();
  }
}
