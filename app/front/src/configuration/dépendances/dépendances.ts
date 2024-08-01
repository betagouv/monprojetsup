import { env } from "@/configuration/environnement";
import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";
import { bacInMemoryRepository } from "@/features/bac/infrastructure/gateway/bacInMemoryRepository/bacInMemoryRepository";
import { RechercherSpécialitésPourUnBacUseCase } from "@/features/bac/usecase/RechercherSpécialitésPourUnBac";
import { RécupérerBacsUseCase } from "@/features/bac/usecase/RécupérerBacs";
import { RécupérerSpécialitésUseCase } from "@/features/bac/usecase/RécupérerSpécialités";
import { type CentreIntêretRepository } from "@/features/centreIntêret/infrastructure/centreIntêretRepository.interface";
import { centreIntêretInMemoryRepository } from "@/features/centreIntêret/infrastructure/gateway/centreIntêretInMemoryRepository/centreIntêretInMemoryRepository";
import { RécupérerCentresIntêretsGroupésParCatégorieUseCase } from "@/features/centreIntêret/usecase/RécupérerCentresIntêretsGroupésParCatégorie";
import { communeHTTPRepository } from "@/features/commune/infrastructure/communeHTTPRepository/communeHTTPRepository";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";
import { RechercherCommunesUseCase } from "@/features/commune/usecase/RechercherCommunes";
import { type DomaineProfessionnelRepository } from "@/features/domaineProfessionnel/infrastructure/domaineProfessionnelRepository.interface";
import { domaineProfessionnelInMemoryRepository } from "@/features/domaineProfessionnel/infrastructure/gateway/domaineProfessionnelInMemoryRepository/domaineProfessionnelInMemoryRepository";
import { RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase } from "@/features/domaineProfessionnel/usecase/RécupérerDomainesProfessionnelsGroupésParCatégorie";
import { ÉlèveHttpRepository } from "@/features/élève/infrastructure/gateway/élèveHttpRepository/élèveHttpRepository";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { ÉlèveSessionStorageRepository } from "@/features/élève/infrastructure/gateway/élèveSessionStorageRepository/élèveSessionStorageRepository";
import { MettreÀJourÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourProfilÉlève";
import { RécupérerÉlèveUseCase } from "@/features/élève/usecase/RécupérerProfilÉlève";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { formationHttpRepository } from "@/features/formation/infrastructure/gateway/formationHttpRepository/formationHttpRepository";
import { formationInMemoryRepository } from "@/features/formation/infrastructure/gateway/formationInMemoryRepository/formationInMemoryRepository";
import { RechercherFormationsUseCase } from "@/features/formation/usecase/RechercherFormations";
import { RécupérerAperçusFormationsUseCase } from "@/features/formation/usecase/RécupérerAperçusFormations";
import { RécupérerDétailFormationUseCase } from "@/features/formation/usecase/RécupérerDétailFormation";
import { métierInMemoryRepository } from "@/features/métier/infrastructure/gateway/métierInMemoryRepository/métierInMemoryRepository";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import { RechercherMétiersUseCase } from "@/features/métier/usecase/RechercherMétiers";
import { RécupérerAperçusMétiersUseCase } from "@/features/métier/usecase/RécupérerAperçusMétiers";
import { HttpClient } from "@/services/httpClient/httpClient";
import { Logger } from "@/services/logger/logger";
import { MpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient";

export class Dépendances {
  private static instance: Dépendances;

  private readonly _logger: Logger;

  private readonly _httpClient: HttpClient;

  private readonly _mpsApiHttpClient: MpsApiHttpClient;

  private readonly _élèveRepository: ÉlèveRepository;

  private readonly _formationRepository: FormationRepository;

  private readonly _formationHttpRepository: FormationRepository;

  private readonly _métierRepository: MétierRepository;

  private readonly _bacRepository: BacRepository;

  private readonly _domaineProfessionnelRepository: DomaineProfessionnelRepository;

  private readonly _centreIntêretRepository: CentreIntêretRepository;

  private readonly _communeRepository: CommuneRepository;

  public readonly mettreÀJourProfilÉlèveUseCase: MettreÀJourÉlèveUseCase;

  public readonly récupérerProfilÉlèveUseCase: RécupérerÉlèveUseCase;

  public readonly récupérerAperçusMétiersUseCase: RécupérerAperçusMétiersUseCase;

  public readonly rechercherMétiersUseCase: RechercherMétiersUseCase;

  public readonly récupérerAperçusFormationsUseCase: RécupérerAperçusFormationsUseCase;

  public readonly récupérerDétailFormationUseCase: RécupérerDétailFormationUseCase;

  public readonly rechercherFormationsUseCase: RechercherFormationsUseCase;

  public readonly rechercherCommunesUseCase: RechercherCommunesUseCase;

  public readonly récupérerBacsUseCase: RécupérerBacsUseCase;

  public readonly récupérerSpécialitésUseCase: RécupérerSpécialitésUseCase;

  public readonly rechercherSpécialitésPourUnBacUseCase: RechercherSpécialitésPourUnBacUseCase;

  public readonly récupérerDomainesProfessionnelsGroupésParCatégorieUseCase: RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase;

  public readonly récupérerCentresIntêretsGroupésParCatégorieUseCase: RécupérerCentresIntêretsGroupésParCatégorieUseCase;

  private constructor() {
    this._logger = new Logger();
    this._httpClient = new HttpClient(this._logger);
    this._mpsApiHttpClient = new MpsApiHttpClient(this._httpClient, env.VITE_API_URL);
    this._élèveRepository = env.VITE_TEST_MODE
      ? new ÉlèveSessionStorageRepository()
      : new ÉlèveHttpRepository(this._mpsApiHttpClient);
    this._formationRepository = new formationInMemoryRepository();
    this._formationHttpRepository = new formationHttpRepository(this._mpsApiHttpClient);
    this._métierRepository = new métierInMemoryRepository();
    this._bacRepository = new bacInMemoryRepository();
    this._domaineProfessionnelRepository = new domaineProfessionnelInMemoryRepository();
    this._centreIntêretRepository = new centreIntêretInMemoryRepository();
    this._communeRepository = new communeHTTPRepository(this._httpClient);

    this.mettreÀJourProfilÉlèveUseCase = new MettreÀJourÉlèveUseCase(this._élèveRepository);
    this.récupérerProfilÉlèveUseCase = new RécupérerÉlèveUseCase(this._élèveRepository);
    this.récupérerAperçusMétiersUseCase = new RécupérerAperçusMétiersUseCase(this._métierRepository);
    this.rechercherMétiersUseCase = new RechercherMétiersUseCase(this._métierRepository);
    this.récupérerAperçusFormationsUseCase = new RécupérerAperçusFormationsUseCase(this._formationRepository);
    this.rechercherFormationsUseCase = new RechercherFormationsUseCase(this._formationRepository);
    this.récupérerDétailFormationUseCase = new RécupérerDétailFormationUseCase(this._formationHttpRepository);
    this.rechercherCommunesUseCase = new RechercherCommunesUseCase(this._communeRepository);
    this.récupérerBacsUseCase = new RécupérerBacsUseCase(this._bacRepository);
    this.récupérerSpécialitésUseCase = new RécupérerSpécialitésUseCase(this._bacRepository);
    this.rechercherSpécialitésPourUnBacUseCase = new RechercherSpécialitésPourUnBacUseCase(this._bacRepository);
    this.récupérerDomainesProfessionnelsGroupésParCatégorieUseCase =
      new RécupérerDomainesProfessionnelsGroupésParCatégorieUseCase(this._domaineProfessionnelRepository);
    this.récupérerCentresIntêretsGroupésParCatégorieUseCase = new RécupérerCentresIntêretsGroupésParCatégorieUseCase(
      this._centreIntêretRepository,
    );
  }

  public static getInstance(): Dépendances {
    if (!Dépendances.instance) {
      Dépendances.instance = new Dépendances();
    }

    return Dépendances.instance;
  }
}

export const dépendances = Dépendances.getInstance();
