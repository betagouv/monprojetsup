import {
  type RécupérerFormationsRéponseHTTP,
  type RécupérerSuggestionsFormationsRéponseHTTP,
} from "./formationHttpRepository.interface";
import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";
import { RessourceNonTrouvéeErreurHttp } from "@/services/erreurs/erreursHttp";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

export class formationHttpRepository implements FormationRepository {
  private _ENDPOINT = "/api/v1/formations" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérer(formationId: string): Promise<Formation | Error> {
    const réponse = await this.récupérerPlusieurs([formationId]);

    if (réponse instanceof RessourceNonTrouvéeErreurHttp) {
      return new RessourceNonTrouvéeErreur();
    }

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse?.[0];
  }

  public async récupérerPlusieurs(formationIds: string[]): Promise<Formation[] | Error> {
    const paramètresDeRequête = new URLSearchParams();

    for (const formationId of formationIds) {
      paramètresDeRequête.append("ids", formationId);
    }

    const réponse = await this._mpsApiHttpClient.get<RécupérerFormationsRéponseHTTP>(
      this._ENDPOINT,
      paramètresDeRequête,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperVersLeDomaine(formation));
  }

  public async rechercher(recherche: string): Promise<Formation[] | Error> {
    const paramètresDeRequête = new URLSearchParams();
    paramètresDeRequête.set("recherche", recherche);

    const réponse = await this._mpsApiHttpClient.get<RécupérerFormationsRéponseHTTP>(
      `${this._ENDPOINT}/recherche/detaillee`,
      paramètresDeRequête,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperVersLeDomaine(formation));
  }

  public async suggérer(): Promise<Formation[] | Error> {
    const réponse = await this._mpsApiHttpClient.get<RécupérerSuggestionsFormationsRéponseHTTP>(
      `${this._ENDPOINT}/suggestions`,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.formations.map((formation) => this._mapperVersLeDomaine(formation));
  }

  private _mapperVersLeDomaine(formationHttp: RécupérerFormationsRéponseHTTP["formations"][number]): Formation {
    const regexLienParcoursSup = /Parcoursup/u;
    return {
      id: formationHttp.formation.id,
      nom: formationHttp.formation.nom,
      descriptifs: {
        formation: formationHttp.formation.descriptifFormation ?? null,
        détails: formationHttp.formation.descriptifDiplome ?? null,
        attendus: formationHttp.formation.descriptifAttendus ?? null,
        conseils: formationHttp.formation.descriptifConseils ?? null,
      },
      estEnAlternance: formationHttp.formation.apprentissage,
      lienParcoursSup: formationHttp.formation.liens.find((lien) => regexLienParcoursSup.exec(lien.nom))?.url ?? null,
      liens: formationHttp.formation.liens.map((lien) => ({ intitulé: lien.nom, url: lien.url })),
      admis: {
        moyenneGénérale: {
          idBac: formationHttp.formation.moyenneGeneraleDesAdmis?.baccalaureat?.id ?? null,
          nomBac: formationHttp.formation.moyenneGeneraleDesAdmis?.baccalaureat?.nom ?? null,
          centiles:
            formationHttp.formation.moyenneGeneraleDesAdmis?.centiles.map((centile) => ({
              centile: centile.centile,
              note: centile.note,
            })) ?? [],
        },
        répartition: {
          parBac:
            formationHttp.formation.repartitionAdmisAnneePrecedente?.parBaccalaureat.map((répartition) => ({
              idBac: répartition.baccalaureat.id,
              nomBac: répartition.baccalaureat.nom,
              nombre: répartition.nombreAdmis,
              pourcentage: Math.round(
                (répartition.nombreAdmis / (formationHttp.formation.repartitionAdmisAnneePrecedente?.total ?? 100)) *
                  100,
              ),
            })) ?? [],
        },
        total: formationHttp.formation.repartitionAdmisAnneePrecedente?.total ?? null,
      },
      formationsAssociées: formationHttp.formation.idsFormationsAssociees,
      critèresAnalyse: formationHttp.formation.criteresAnalyseCandidature.map((critère) => ({
        nom: critère.nom,
        pourcentage: critère.pourcentage,
      })),
      établissements: formationHttp.formation.voeux.map((établissement) => ({
        id: établissement.id,
        nom: établissement.nom,
        commune: { nom: établissement.commune.nom, code: établissement.commune.codeInsee },
      })),
      établissementsParCommuneFavorites: formationHttp.formation.communesFavoritesAvecLeursVoeux.map((commune) => ({
        commune: {
          code: commune.commune.codeInsee,
          nom: commune.commune.nom,
        },
        établissements: commune.voeuxAvecDistance.map((établissement) => ({
          id: établissement.voeu.id,
          nom: établissement.voeu.nom,
          distanceEnKm: établissement.distanceKm,
        })),
      })),
      communesProposantLaFormation: this._extraireCommunesDesÉtablissements(formationHttp.formation.voeux),
      métiersAccessibles: formationHttp.formation.metiers.map((métier) => ({
        id: métier.id,
        nom: `${métier.nom[0].toUpperCase()}${métier.nom.slice(1)}`,
        descriptif: métier.descriptif ?? null,
        liens: métier.liens.map((lien) => ({ intitulé: lien.nom, url: lien.url })),
      })),
      explications: this._mapperLesExplications(formationHttp.explications),
      affinité: this._calculerNombrePointsAffinité(formationHttp.explications),
    };
  }

  private _mapperLesExplications = (
    explications: RécupérerFormationsRéponseHTTP["formations"][number]["explications"],
  ): Formation["explications"] => {
    if (!explications) {
      return null;
    }

    return {
      communes:
        explications.geographique.map((commune) => ({
          nom: commune.nomVille,
          distanceKm: commune.distanceKm,
        })) ?? [],
      formationsSimilaires:
        explications.formationsSimilaires.map((formation) => ({
          id: formation.id,
          nom: formation.nom,
        })) ?? [],
      duréeÉtudesPrévue: explications.dureeEtudesPrevue ?? null,
      alternance: explications.alternance ?? null,
      choixÉlève: {
        intérêts:
          explications.choixEleve?.interets.map((intérêt) => ({
            id: intérêt.id,
            nom: intérêt.nom,
          })) ?? [],
        domaines:
          explications.choixEleve?.domaines.map((domaine) => ({
            id: domaine.id,
            nom: domaine.nom,
          })) ?? [],
        métiers:
          explications.choixEleve?.metiers.map((métier) => ({
            id: métier.id,
            nom: métier.nom,
          })) ?? [],
      },
      spécialitésChoisies: explications.specialitesChoisies.map((spécialité) => ({
        nom: spécialité.nomSpecialite,
        pourcentageAdmisAnnéePrécédente: spécialité.pourcentage,
      })),
      typeBaccalaureat: explications.typeBaccalaureat
        ? {
            id: explications.typeBaccalaureat.baccalaureat.id,
            nom: explications.typeBaccalaureat.baccalaureat.nom,
            pourcentageAdmisAnnéePrécédente: explications.typeBaccalaureat?.pourcentage,
          }
        : null,
      autoEvaluationMoyenne: explications.autoEvaluationMoyenne
        ? {
            moyenne: explications.autoEvaluationMoyenne.moyenne,
            intervalBas: explications.autoEvaluationMoyenne.basIntervalleNotes,
            intervalHaut: explications.autoEvaluationMoyenne.hautIntervalleNotes,
            idBacUtilisé: explications.autoEvaluationMoyenne.baccalaureatUtilise.id,
            nomBacUtilisé: explications.autoEvaluationMoyenne.baccalaureatUtilise.nom,
          }
        : null,
      explicationsCalcul: explications.detailsCalculScore?.details ?? null,
    };
  };

  private _calculerNombrePointsAffinité = (
    explications: RécupérerFormationsRéponseHTTP["formations"][number]["explications"],
  ): number => {
    if (!explications) {
      return 0;
    }

    const conditionsDeValidationExplication = [
      explications.geographique.length > 0,
      explications.formationsSimilaires.length > 0,
      explications.dureeEtudesPrevue,
      explications.alternance,
      explications.choixEleve &&
        (explications.choixEleve.domaines.length > 0 ||
          explications.choixEleve.interets.length > 0 ||
          explications.choixEleve.metiers.length > 0),
      explications.specialitesChoisies.length > 0,
      explications.typeBaccalaureat,
      explications.autoEvaluationMoyenne,
    ];

    let points = 0;

    for (const condition of conditionsDeValidationExplication) {
      if (condition) {
        points++;
      }
    }

    return points;
  };

  private _extraireCommunesDesÉtablissements(
    établissements: RécupérerFormationsRéponseHTTP["formations"][number]["formation"]["voeux"],
  ): string[] {
    const nomsCommunes = établissements.map((établissement) => établissement.commune.nom);
    return [...new Set(nomsCommunes)];
  }
}
