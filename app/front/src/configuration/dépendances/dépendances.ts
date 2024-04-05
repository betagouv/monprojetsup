import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { élèveSessionStorageRepository } from "@/features/élève/infrastructure/gateway/élèveSessionStorageRepository/élèveSessionStorageRepository";
import { CréerÉlèveUseCase } from "@/features/élève/usecase/CréerÉlève";
import { MettreÀJourÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourÉlève";
import { RécupérerÉlèveUseCase } from "@/features/élève/usecase/RécupérerÉlève";

export class Dépendances {
  private static instance: Dépendances;

  public readonly _élèveRepository: ÉlèveRepository;

  public readonly créerÉlèveUseCase: CréerÉlèveUseCase;

  public readonly mettreÀJourÉlèveUseCase: MettreÀJourÉlèveUseCase;

  public readonly récupérerÉlèveUseCase: RécupérerÉlèveUseCase;

  private constructor() {
    this._élèveRepository = new élèveSessionStorageRepository();
    this.créerÉlèveUseCase = new CréerÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourÉlèveUseCase = new MettreÀJourÉlèveUseCase(this._élèveRepository);
    this.récupérerÉlèveUseCase = new RécupérerÉlèveUseCase(this._élèveRepository);
  }

  public static getInstance(): Dépendances {
    if (!Dépendances.instance) {
      Dépendances.instance = new Dépendances();
    }

    return Dépendances.instance;
  }
}

export const dépendances = Dépendances.getInstance();
