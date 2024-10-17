import { environnement } from "@/configuration/environnement";
import { communeHttpRepository } from "@/features/commune/infrastructure/communeHttpRepository/communeHttpRepository";
import { communeInMemoryRepository } from "@/features/commune/infrastructure/communeInMemoryRepository/communeInMemoryRepository";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";
import { RechercherCommunesUseCase } from "@/features/commune/usecase/RechercherCommunes";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { formationHttpRepository } from "@/features/formation/infrastructure/gateway/formationHttpRepository/formationHttpRepository";
import { formationInMemoryRepository } from "@/features/formation/infrastructure/gateway/formationInMemoryRepository/formationInMemoryRepository";
import { RechercherFormationsUseCase } from "@/features/formation/usecase/RechercherFormations";
import { RécupérerFormationUseCase } from "@/features/formation/usecase/RécupérerFormation";
import { RécupérerFormationsUseCase } from "@/features/formation/usecase/RécupérerFormations";
import { SuggérerFormationsUseCase } from "@/features/formation/usecase/SuggérerFormations";
import { métierHttpRepository } from "@/features/métier/infrastructure/gateway/métierHttpRepository/métierHttpRepository";
import { métierInMemoryRepository } from "@/features/métier/infrastructure/gateway/métierInMemoryRepository/métierInMemoryRepository";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import { RechercherMétiersUseCase } from "@/features/métier/usecase/RechercherMétiers";
import { RécupérerMétierUseCase } from "@/features/métier/usecase/RécupérerMétier";
import { RécupérerMétiersUseCase } from "@/features/métier/usecase/RécupérerMétiers";
import { RéférentielDonnéesHttpRepository } from "@/features/référentielDonnées/infrastructure/gateway/référentielDonnéesHttpRepository/référentielDonnéesHttpRepository";
import { RéférentielDonnéesInMemoryRepository } from "@/features/référentielDonnées/infrastructure/gateway/référentielDonnéesInMemoryRepository/référentielDonnéesInMemoryRepository";
import { type RéférentielDonnéesRepository } from "@/features/référentielDonnées/infrastructure/référentielDonnéesRepository.interface";
import { RécupérerRéférentielDonnéesUseCase } from "@/features/référentielDonnées/usecase/RécupérerRéférentielDonnées";
import { ÉlèveHttpRepository } from "@/features/élève/infrastructure/gateway/élèveHttpRepository/élèveHttpRepository";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { ÉlèveSessionStorageRepository } from "@/features/élève/infrastructure/gateway/élèveSessionStorageRepository/élèveSessionStorageRepository";
import { AssocierCompteParcourSupÉlèveUseCase } from "@/features/élève/usecase/AssocierCompteParcourSupÉlève";
import { MettreÀJourÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourProfilÉlève";
import { RécupérerÉlèveUseCase } from "@/features/élève/usecase/RécupérerProfilÉlève";
import { HttpClient } from "@/services/httpClient/httpClient";
import { Logger } from "@/services/logger/logger";
import { MpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient";

export class Dépendances {
  // eslint-disable-next-line no-use-before-define
  private static instance: Dépendances;

  private readonly _logger: Logger;

  private readonly _httpClient: HttpClient;

  private readonly _mpsApiHttpClient: MpsApiHttpClient;

  private readonly _référentielDonnéesRepository: RéférentielDonnéesRepository;

  private readonly _élèveRepository: ÉlèveRepository;

  private readonly _formationRepository: FormationRepository;

  private readonly _métierRepository: MétierRepository;

  private readonly _communeRepository: CommuneRepository;

  public readonly récupérerRéférentielDonnéesUseCase: RécupérerRéférentielDonnéesUseCase;

  public readonly mettreÀJourProfilÉlèveUseCase: MettreÀJourÉlèveUseCase;

  public readonly récupérerProfilÉlèveUseCase: RécupérerÉlèveUseCase;

  public readonly associerCompteParcourSupÉlèveUseCase: AssocierCompteParcourSupÉlèveUseCase;

  public readonly récupérerFormationUseCase: RécupérerFormationUseCase;

  public readonly récupérerFormationsUseCase: RécupérerFormationsUseCase;

  public readonly rechercherFormationsUseCase: RechercherFormationsUseCase;

  public readonly suggérerFormationsUseCase: SuggérerFormationsUseCase;

  public readonly récupérerMétierUseCase: RécupérerMétierUseCase;

  public readonly récupérerMétiersUseCase: RécupérerMétiersUseCase;

  public readonly rechercherMétiersUseCase: RechercherMétiersUseCase;

  public readonly rechercherCommunesUseCase: RechercherCommunesUseCase;

  private constructor() {
    this._logger = new Logger();
    this._httpClient = new HttpClient(this._logger);
    this._mpsApiHttpClient = new MpsApiHttpClient(this._httpClient, environnement.VITE_API_URL);

    // Repositories
    this._référentielDonnéesRepository = environnement.VITE_TEST_MODE
      ? new RéférentielDonnéesInMemoryRepository()
      : new RéférentielDonnéesHttpRepository(this._mpsApiHttpClient);
    this._élèveRepository = environnement.VITE_TEST_MODE
      ? new ÉlèveSessionStorageRepository()
      : new ÉlèveHttpRepository(this._mpsApiHttpClient);
    this._formationRepository = environnement.VITE_TEST_MODE
      ? new formationInMemoryRepository()
      : new formationHttpRepository(this._mpsApiHttpClient);
    this._métierRepository = environnement.VITE_TEST_MODE
      ? new métierInMemoryRepository()
      : new métierHttpRepository(this._mpsApiHttpClient);
    this._communeRepository = environnement.VITE_TEST_MODE
      ? new communeInMemoryRepository()
      : new communeHttpRepository(this._httpClient);

    // Référentiel de données
    this.récupérerRéférentielDonnéesUseCase = new RécupérerRéférentielDonnéesUseCase(
      this._référentielDonnéesRepository,
    );

    // Élève
    this.mettreÀJourProfilÉlèveUseCase = new MettreÀJourÉlèveUseCase(this._élèveRepository);
    this.récupérerProfilÉlèveUseCase = new RécupérerÉlèveUseCase(this._élèveRepository);
    this.associerCompteParcourSupÉlèveUseCase = new AssocierCompteParcourSupÉlèveUseCase(this._élèveRepository);

    // Formations
    this.récupérerFormationUseCase = new RécupérerFormationUseCase(this._formationRepository);
    this.récupérerFormationsUseCase = new RécupérerFormationsUseCase(this._formationRepository);
    this.rechercherFormationsUseCase = new RechercherFormationsUseCase(this._formationRepository);
    this.suggérerFormationsUseCase = new SuggérerFormationsUseCase(this._formationRepository);

    // Métiers
    this.récupérerMétierUseCase = new RécupérerMétierUseCase(this._métierRepository);
    this.récupérerMétiersUseCase = new RécupérerMétiersUseCase(this._métierRepository);
    this.rechercherMétiersUseCase = new RechercherMétiersUseCase(this._métierRepository);

    // Communes
    this.rechercherCommunesUseCase = new RechercherCommunesUseCase(this._communeRepository);
  }

  public static getInstance(): Dépendances {
    if (!Dépendances.instance) {
      Dépendances.instance = new Dépendances();
    }

    return Dépendances.instance;
  }
}

export const dépendances = Dépendances.getInstance();
