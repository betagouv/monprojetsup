import { type RécupérerFormationRéponseHTTP } from "./formationHttpRepository.interface";
import { env } from "@/configuration/environnement";
import { type AlternanceÉlève, type DuréeÉtudesPrévueÉlève } from "@/features/élève/domain/élève.interface";
import { type Formation, type FormationAperçu } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { type HttpClient } from "@/services/httpClient/httpClient";

export class formationHttpRepository implements FormationRepository {
  private ENDPOINT_BASE = `${env.VITE_API_URL}/api/v1/formations/`;

  public constructor(private _httpClient: HttpClient) {}

  public async rechercher(_recherche: string): Promise<FormationAperçu[] | undefined> {
    return undefined;
  }

  public async récupérerAperçus(_formationIds: string[]): Promise<FormationAperçu[] | undefined> {
    return undefined;
  }

  public async récupérer(formationId: string): Promise<Formation | undefined> {
    const réponse = await this._httpClient.récupérer<RécupérerFormationRéponseHTTP>({
      endpoint: `${this.ENDPOINT_BASE}${formationId}`,
      méthode: "POST",
      body: {
        profil: null,
      },
    });

    if (!réponse) return undefined;

    return this._mapperVersLeDomaine(réponse);
  }

  private _mapperVersLeDomaine(formationHttp: RécupérerFormationRéponseHTTP): Formation {
    return {
      id: formationHttp.formation.id,
      nom: formationHttp.formation.nom,
      descriptifs: {
        formation: formationHttp.formation.descriptifFormation ?? null,
        détails: formationHttp.formation.descriptifDiplome ?? null,
        attendus: formationHttp.formation.descriptifAttendus ?? null,
        conseils: formationHttp.formation.descriptifConseils ?? null,
      },
      liens: formationHttp.formation.liens.map((lien) => ({
        intitulé: lien.nom,
        url: lien.url,
      })),
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
      villes: formationHttp.formation.villes,
      métiersAccessibles: formationHttp.formation.metiers.map((métier) => ({
        id: métier.id,
        nom: métier.nom,
        descriptif: métier.descriptif ?? null,
        liens: métier.liens.map((lien) => ({ intitulé: lien.nom, url: lien.url })),
      })),
      affinité: formationHttp.formation.tauxAffinite ?? null,
      explications: formationHttp.explications
        ? {
            villes:
              formationHttp.explications.geographique.map((ville) => ({
                nom: ville.nomVille,
                distanceKm: ville.distanceKm,
              })) ?? [],
            formationsSimilaires:
              formationHttp.explications.formationsSimilaires.map((formation) => ({
                id: formation.id,
                nom: formation.nom,
              })) ?? [],
            duréeÉtudesPrévue: (formationHttp.explications.dureeEtudesPrevue as DuréeÉtudesPrévueÉlève) ?? null,
            alternance: (formationHttp.explications.alternance as AlternanceÉlève) ?? null,
            intêretsEtDomainesChoisis: {
              intêrets:
                formationHttp.explications.interetsEtDomainesChoisis?.interets.map((intêret) => ({
                  id: intêret.id,
                  nom: intêret.nom,
                })) ?? [],
              domaines:
                formationHttp.explications.interetsEtDomainesChoisis?.domaines.map((domaine) => ({
                  id: domaine.id,
                  nom: domaine.nom,
                })) ?? [],
            },
            spécialitésChoisies: formationHttp.explications.specialitesChoisies.map((spécialité) => ({
              nom: spécialité.nomSpecialite,
              pourcentageAdmisAnnéePrécédente: spécialité.pourcentage,
            })),
            typeBaccalaureat: formationHttp.explications.typeBaccalaureat
              ? {
                  id: formationHttp.explications.typeBaccalaureat.baccalaureat.id,
                  nom: formationHttp.explications.typeBaccalaureat.baccalaureat.nom,
                  pourcentageAdmisAnnéePrécédente: formationHttp.explications.typeBaccalaureat?.pourcentage,
                }
              : null,
            autoEvaluationMoyenne: formationHttp.explications.autoEvaluationMoyenne
              ? {
                  moyenne: formationHttp.explications.autoEvaluationMoyenne.moyenne,
                  intervalBas: formationHttp.explications.autoEvaluationMoyenne.basIntervalleNotes,
                  intervalHaut: formationHttp.explications.autoEvaluationMoyenne.hautIntervalleNotes,
                  idBacUtilisé: formationHttp.explications.autoEvaluationMoyenne.baccalaureatUtilise.id,
                  nomBacUtilisé: formationHttp.explications.autoEvaluationMoyenne.baccalaureatUtilise.nom,
                }
              : null,
          }
        : null,
    };
  }
}
