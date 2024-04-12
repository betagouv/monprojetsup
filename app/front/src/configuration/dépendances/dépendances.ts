import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { élèveSessionStorageRepository } from "@/features/élève/infrastructure/gateway/élèveSessionStorageRepository/élèveSessionStorageRepository";
import { CréerÉlèveUseCase } from "@/features/élève/usecase/CréerÉlève";
import { MettreÀJourÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourÉlève";
import { RécupérerÉlèveUseCase } from "@/features/élève/usecase/RécupérerÉlève";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { formationInMemoryRepository } from "@/features/formation/infrastructure/gateway/formationInMemoryRepository/formationInMemoryRepository";
import { RécupérerFormationsUseCase } from "@/features/formation/usecase/RécupérerFormations";
import { métierInMemoryRepository } from "@/features/métier/infrastructure/gateway/métierInMemoryRepository/métierInMemoryRepository";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import { RécupérerMétiersUseCase } from "@/features/métier/usecase/RécupérerMétiers";

export class Dépendances {
  private static instance: Dépendances;

  private readonly _élèveRepository: ÉlèveRepository;

  private readonly _formationRepository: FormationRepository;

  private readonly _métierRepository: MétierRepository;

  public readonly créerÉlèveUseCase: CréerÉlèveUseCase;

  public readonly mettreÀJourÉlèveUseCase: MettreÀJourÉlèveUseCase;

  public readonly récupérerÉlèveUseCase: RécupérerÉlèveUseCase;

  public readonly récupérerFormationsUseCase: RécupérerFormationsUseCase;

  public readonly récupérerMétiersUseCase: RécupérerMétiersUseCase;

  private constructor() {
    this._élèveRepository = new élèveSessionStorageRepository();
    this._formationRepository = new formationInMemoryRepository();
    this._métierRepository = new métierInMemoryRepository();
    this.créerÉlèveUseCase = new CréerÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourÉlèveUseCase = new MettreÀJourÉlèveUseCase(this._élèveRepository);
    this.récupérerÉlèveUseCase = new RécupérerÉlèveUseCase(this._élèveRepository);
    this.récupérerFormationsUseCase = new RécupérerFormationsUseCase(this._formationRepository);
    this.récupérerMétiersUseCase = new RécupérerMétiersUseCase(this._métierRepository);
  }

  public static getInstance(): Dépendances {
    if (!Dépendances.instance) {
      Dépendances.instance = new Dépendances();
    }

    return Dépendances.instance;
  }
}

export const dépendances = Dépendances.getInstance();
