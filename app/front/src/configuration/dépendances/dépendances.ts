import { environnement } from "@/configuration/environnement";
import { AnalyticsRepository } from "@/features/analytics/infrastructure/analytics.interface";
import { AnalyticsConsoleRepository } from "@/features/analytics/infrastructure/analyticsConsoleRepository/analyticsConsoleRepository";
import { AnalyticsMatomoRepository } from "@/features/analytics/infrastructure/analyticsMatomoRepository/analyticsMatomoRepository";
import { communeHttpRepository } from "@/features/commune/infrastructure/communeHttpRepository/communeHttpRepository";
import { communeInMemoryRepository } from "@/features/commune/infrastructure/communeInMemoryRepository/communeInMemoryRepository";
import { type CommuneRepository } from "@/features/commune/infrastructure/communeRepository.interface";
import { RechercherCommunesUseCase } from "@/features/commune/usecase/RechercherCommunes";
import { ÉlèveHttpRepository } from "@/features/élève/infrastructure/gateway/élèveHttpRepository/élèveHttpRepository";
import { type ÉlèveRepository } from "@/features/élève/infrastructure/gateway/élèveRepository.interface";
import { ÉlèveSessionStorageRepository } from "@/features/élève/infrastructure/gateway/élèveSessionStorageRepository/élèveSessionStorageRepository";
import { AssocierCompteParcourSupÉlèveUseCase } from "@/features/élève/usecase/AssocierCompteParcourSupÉlève";
import { MettreÀJourAmbitionsÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourAmbitionsÉlève";
import { MettreÀJourCommunesÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourCommunesÉlève";
import { MettreÀJourFormationsÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourFormationsÉlève";
import { MettreÀJourFormationsMasquéesÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourFormationsMasquéesÉlève";
import { MettreÀJourMétiersÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourMétiersÉlève";
import { MettreÀJourNotesPersonnellesÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourNotesPersonnellesÉlève";
import { MettreÀJourProfilÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourProfilÉlève";
import { MettreÀJourSpécialitésÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourSpécialitésÉlève";
import { MettreÀJourVoeuxÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourVoeuxÉlève";
import { RechercherSpécialitésUseCase } from "@/features/élève/usecase/RechercherSpécialités";
import { RécupérerÉlèveUseCase } from "@/features/élève/usecase/RécupérerProfilÉlève";
import { SupprimerTousLesMétiersÉlèveUseCase } from "@/features/élève/usecase/SupprimerTousLesMétiersÉlève";
import { SupprimerToutesLesFormationsÉlèveUseCase } from "@/features/élève/usecase/SupprimerToutesLesFormationsÉlève";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { formationHttpRepository } from "@/features/formation/infrastructure/gateway/formationHttpRepository/formationHttpRepository";
import { formationInMemoryRepository } from "@/features/formation/infrastructure/gateway/formationInMemoryRepository/formationInMemoryRepository";
import { RechercherFichesFormationsUseCase } from "@/features/formation/usecase/RechercherFichesFormations.ts";
import { RechercherFormationsUseCase } from "@/features/formation/usecase/RechercherFormations.ts";
import { RechercherVoeuxUseCase } from "@/features/formation/usecase/RechercherVoeux";
import { RécupérerFicheFormationUseCase } from "@/features/formation/usecase/RécupérerFicheFormation.ts";
import { RécupérerFichesFormationsUseCase } from "@/features/formation/usecase/RécupérerFichesFormations.ts";
import { RécupérerFormationsUseCase } from "@/features/formation/usecase/RécupérerFormations.ts";
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
import { HttpClient } from "@/services/httpClient/httpClient";
import { ConsoleLogger } from "@/services/logger/consoleLogger/consoleLogger";
import { Logger } from "@/services/logger/logger.interface";
import { SentryLogger } from "@/services/logger/sentryLogger/sentryLogger";
import { MpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient";

export class Dépendances {
  // eslint-disable-next-line no-use-before-define
  private static _instance: Dépendances;

  private readonly _httpClient: HttpClient;

  private readonly _mpsApiHttpClient: MpsApiHttpClient;

  private readonly _référentielDonnéesRepository: RéférentielDonnéesRepository;

  private readonly _élèveRepository: ÉlèveRepository;

  private readonly _formationRepository: FormationRepository;

  private readonly _métierRepository: MétierRepository;

  private readonly _communeRepository: CommuneRepository;

  public readonly analyticsRepository: AnalyticsRepository;

  public readonly logger: Logger;

  public readonly récupérerRéférentielDonnéesUseCase: RécupérerRéférentielDonnéesUseCase;

  public readonly mettreÀJourProfilÉlèveUseCase: MettreÀJourProfilÉlèveUseCase;

  public readonly récupérerProfilÉlèveUseCase: RécupérerÉlèveUseCase;

  public readonly associerCompteParcourSupÉlèveUseCase: AssocierCompteParcourSupÉlèveUseCase;

  public readonly mettreÀJourSpécialitésÉlèveUseCase: MettreÀJourSpécialitésÉlèveUseCase;

  public readonly mettreÀJourVoeuxÉlèveUseCase: MettreÀJourVoeuxÉlèveUseCase;

  public readonly mettreÀJourCommunesÉlèveUseCase: MettreÀJourCommunesÉlèveUseCase;

  public readonly mettreÀJourFormationsÉlèveUseCase: MettreÀJourFormationsÉlèveUseCase;

  public readonly mettreÀJourMétiersÉlèveUseCase: MettreÀJourMétiersÉlèveUseCase;

  public readonly mettreÀJourFormationsMasquéesÉlèveUseCase: MettreÀJourFormationsMasquéesÉlèveUseCase;

  public readonly mettreÀJourNotesPersonnellesÉlèveUseCase: MettreÀJourNotesPersonnellesÉlèveUseCase;

  public readonly mettreÀJourAmbitionsÉlèveUseCase: MettreÀJourAmbitionsÉlèveUseCase;

  public readonly supprimerTousLesMétiersÉlèveUseCase: SupprimerTousLesMétiersÉlèveUseCase;

  public readonly supprimerToutesLesFormationsÉlèveUseCase: SupprimerToutesLesFormationsÉlèveUseCase;

  public readonly récupérerFicheFormationUseCase: RécupérerFicheFormationUseCase;

  public readonly récupérerFichesFormationsUseCase: RécupérerFichesFormationsUseCase;

  public readonly récupérerFormationsUseCase: RécupérerFormationsUseCase;

  public readonly rechercherFichesFormationsUseCase: RechercherFichesFormationsUseCase;

  public readonly rechercherFormationsUseCase: RechercherFormationsUseCase;

  public readonly suggérerFormationsUseCase: SuggérerFormationsUseCase;

  public readonly récupérerMétierUseCase: RécupérerMétierUseCase;

  public readonly récupérerMétiersUseCase: RécupérerMétiersUseCase;

  public readonly rechercherMétiersUseCase: RechercherMétiersUseCase;

  public readonly rechercherCommunesUseCase: RechercherCommunesUseCase;

  public readonly rechercherSpécialitésUseCase: RechercherSpécialitésUseCase;

  public readonly rechercherVoeuxUseCase: RechercherVoeuxUseCase;

  private constructor() {
    this._httpClient = new HttpClient();
    this._mpsApiHttpClient = new MpsApiHttpClient(this._httpClient, environnement.VITE_API_URL);

    // Repositories
    this.analyticsRepository =
      environnement.VITE_TEST_MODE || !environnement.VITE_MATOMO_SITE_ID
        ? new AnalyticsConsoleRepository()
        : new AnalyticsMatomoRepository();
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

    // Logger
    this.logger = environnement.VITE_SENTRY_DSN ? new SentryLogger() : new ConsoleLogger();

    // Référentiel de données
    this.récupérerRéférentielDonnéesUseCase = new RécupérerRéférentielDonnéesUseCase(
      this._référentielDonnéesRepository,
    );

    // Élève
    this.mettreÀJourProfilÉlèveUseCase = new MettreÀJourProfilÉlèveUseCase(this._élèveRepository);
    this.récupérerProfilÉlèveUseCase = new RécupérerÉlèveUseCase(this._élèveRepository);
    this.associerCompteParcourSupÉlèveUseCase = new AssocierCompteParcourSupÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourSpécialitésÉlèveUseCase = new MettreÀJourSpécialitésÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourVoeuxÉlèveUseCase = new MettreÀJourVoeuxÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourCommunesÉlèveUseCase = new MettreÀJourCommunesÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourFormationsÉlèveUseCase = new MettreÀJourFormationsÉlèveUseCase(
      this._élèveRepository,
      this.analyticsRepository,
    );
    this.mettreÀJourMétiersÉlèveUseCase = new MettreÀJourMétiersÉlèveUseCase(
      this._élèveRepository,
      this.analyticsRepository,
    );
    this.mettreÀJourFormationsMasquéesÉlèveUseCase = new MettreÀJourFormationsMasquéesÉlèveUseCase(
      this._élèveRepository,
    );
    this.mettreÀJourNotesPersonnellesÉlèveUseCase = new MettreÀJourNotesPersonnellesÉlèveUseCase(this._élèveRepository);
    this.mettreÀJourAmbitionsÉlèveUseCase = new MettreÀJourAmbitionsÉlèveUseCase(this._élèveRepository);
    this.supprimerTousLesMétiersÉlèveUseCase = new SupprimerTousLesMétiersÉlèveUseCase(this._élèveRepository);
    this.supprimerToutesLesFormationsÉlèveUseCase = new SupprimerToutesLesFormationsÉlèveUseCase(this._élèveRepository);

    // Formations
    this.récupérerFicheFormationUseCase = new RécupérerFicheFormationUseCase(this._formationRepository);
    this.récupérerFichesFormationsUseCase = new RécupérerFichesFormationsUseCase(this._formationRepository);
    this.rechercherFichesFormationsUseCase = new RechercherFichesFormationsUseCase(this._formationRepository);
    this.récupérerFormationsUseCase = new RécupérerFormationsUseCase(this._formationRepository);
    this.rechercherFormationsUseCase = new RechercherFormationsUseCase(this._formationRepository);
    this.suggérerFormationsUseCase = new SuggérerFormationsUseCase(this._formationRepository);

    // Métiers
    this.récupérerMétierUseCase = new RécupérerMétierUseCase(this._métierRepository);
    this.récupérerMétiersUseCase = new RécupérerMétiersUseCase(this._métierRepository);
    this.rechercherMétiersUseCase = new RechercherMétiersUseCase(this._métierRepository);

    // Communes
    this.rechercherCommunesUseCase = new RechercherCommunesUseCase(this._communeRepository);

    // Spécialités
    this.rechercherSpécialitésUseCase = new RechercherSpécialitésUseCase();

    // Voeux
    this.rechercherVoeuxUseCase = new RechercherVoeuxUseCase();
  }

  public static getInstance(): Dépendances {
    if (!Dépendances._instance) {
      Dépendances._instance = new Dépendances();
    }

    return Dépendances._instance;
  }
}

export const dépendances = Dépendances.getInstance();
