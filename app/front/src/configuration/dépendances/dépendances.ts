import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";
import { bacInMemoryRepository } from "@/features/bac/infrastructure/gateway/bacInMemoryRepository/bacInMemoryRepository";
import { RécupérerBacsUseCase } from "@/features/bac/usecase/RécupérerBacs";
import { RécupérerSpécialitésPourUnBacUseCase } from "@/features/bac/usecase/RécupérerSpécialitésPourUnBac";
import { type DomaineProfessionnelRepository } from "@/features/domaineProfessionnel/infrastructure/domaineProfessionnelRepository.interface";
import { domaineProfessionnelInMemoryRepository } from "@/features/domaineProfessionnel/infrastructure/gateway/domaineProfessionnelInMemoryRepository/domaineProfessionnelInMemoryRepository";
import { RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase } from "@/features/domaineProfessionnel/usecase/RécupérerDomainesProfessionnelsGroupésParCatégorie";
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

  private readonly _bacRepository: BacRepository;

  private readonly _domaineProfessionnelRepository: DomaineProfessionnelRepository;

  public readonly créerÉlèveUseCase: CréerÉlèveUseCase;

  public readonly mettreÀJourÉlèveUseCase: MettreÀJourÉlèveUseCase;

  public readonly récupérerÉlèveUseCase: RécupérerÉlèveUseCase;

  public readonly récupérerFormationsUseCase: RécupérerFormationsUseCase;

  public readonly récupérerMétiersUseCase: RécupérerMétiersUseCase;

  public readonly récupérerBacsUseCase: RécupérerBacsUseCase;

  public readonly récupérerSpécialitésPourUnBacUseCase: RécupérerSpécialitésPourUnBacUseCase;

  public readonly récupérerDomainesProfessionnelsGroupésParCatégorieUseCase: RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase;

  private constructor() {
    this._élèveRepository = new élèveSessionStorageRepository();
    this._formationRepository = new formationInMemoryRepository();
    this._métierRepository = new métierInMemoryRepository();
    this._bacRepository = new bacInMemoryRepository();
    this._domaineProfessionnelRepository = new domaineProfessionnelInMemoryRepository();

    this.créerÉlèveUseCase = new CréerÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourÉlèveUseCase = new MettreÀJourÉlèveUseCase(this._élèveRepository);
    this.récupérerÉlèveUseCase = new RécupérerÉlèveUseCase(this._élèveRepository);
    this.récupérerFormationsUseCase = new RécupérerFormationsUseCase(this._formationRepository);
    this.récupérerMétiersUseCase = new RécupérerMétiersUseCase(this._métierRepository);
    this.récupérerBacsUseCase = new RécupérerBacsUseCase(this._bacRepository);
    this.récupérerSpécialitésPourUnBacUseCase = new RécupérerSpécialitésPourUnBacUseCase(this._bacRepository);
    this.récupérerDomainesProfessionnelsGroupésParCatégorieUseCase =
      new RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase(this._domaineProfessionnelRepository);
  }

  public static getInstance(): Dépendances {
    if (!Dépendances.instance) {
      Dépendances.instance = new Dépendances();
    }

    return Dépendances.instance;
  }
}

export const dépendances = Dépendances.getInstance();
