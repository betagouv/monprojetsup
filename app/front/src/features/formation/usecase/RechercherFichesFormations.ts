import { type FicheFormation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { type TraceService } from "@/services/trace/trace.interface";

export class RechercherFichesFormationsUseCase {
  public constructor(
    private readonly _formationRepository: FormationRepository,
    private readonly _traceService: TraceService
  ) {}

  public async run(recherche: string): Promise<FicheFormation[] | Error> {
    this._traceService.ajouterTraceRechercheFormation(recherche);
    return await this._formationRepository.rechercherFichesFormations(recherche);
  }
}
