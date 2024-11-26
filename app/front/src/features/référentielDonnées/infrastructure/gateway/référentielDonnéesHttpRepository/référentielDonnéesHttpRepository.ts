import { type RécupérerRéférentielDonnéesRéponseHTTP } from "./référentielDonnéesHttpRepository.interface";
import { BacÉlève, type RéférentielDonnées } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type RéférentielDonnéesRepository } from "@/features/référentielDonnées/infrastructure/référentielDonnéesRepository.interface";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";
import { trierTableauDObjetsParOrdreAlphabétique } from "@/utils/array";

export class RéférentielDonnéesHttpRepository implements RéférentielDonnéesRepository {
  private _ENDPOINT = "/api/v1/referentiel" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérer(): Promise<RéférentielDonnées | Error> {
    const réponse = await this._mpsApiHttpClient.get<RécupérerRéférentielDonnéesRéponseHTTP>(this._ENDPOINT);

    if (réponse instanceof Error) {
      return réponse;
    }

    return this._mapperVersLeDomaine(réponse);
  }

  private _mapperVersLeDomaine(référentielDonnéesHttp: RécupérerRéférentielDonnéesRéponseHTTP): RéférentielDonnées {
    const référentielDonnées = {
      élève: {
        situations: référentielDonnéesHttp.situations,
        classes: référentielDonnéesHttp.choixNiveau,
        duréesÉtudesPrévue: référentielDonnéesHttp.choixDureeEtudesPrevue,
        alternances: référentielDonnéesHttp.choixAlternance,
      },
      bacs: référentielDonnéesHttp.baccalaureatsAvecLeurSpecialites.map((bac) => {
        const statiquesAdmission = référentielDonnéesHttp.admissionsParcoursup.parBaccalaureat.find(
          (baccalauréat) => baccalauréat.baccalaureat.id === bac.baccalaureat.id,
        );

        return {
          id: bac.baccalaureat.id as BacÉlève,
          nom: bac.baccalaureat.nom,
          idCarteParcoursup: bac.baccalaureat.idCarteParcoursup,
          spécialités: bac.specialites,
          statistiquesAdmission: {
            parMoyenneGénérale:
              statiquesAdmission?.pourcentages.map((répartition) => ({
                moyenne: répartition.note,
                pourcentageAdmisAyantCetteMoyenneOuMoins: répartition.pourcentageAdmisAyantCetteMoyenneOuMoins,
              })) ?? [],
          },
        };
      }),
      centresIntérêts: référentielDonnéesHttp.categoriesDInteretsAvecLeursSousCategories.map((centreIntérêt) => ({
        id: centreIntérêt.categorieInteret.id,
        nom: centreIntérêt.categorieInteret.nom,
        emoji: centreIntérêt.categorieInteret.emoji,
        sousCatégoriesCentreIntérêt: trierTableauDObjetsParOrdreAlphabétique(
          centreIntérêt.sousCategoriesInterets,
          "nom",
        ).map((intérêt) => ({
          id: intérêt.id,
          nom: intérêt.nom,
          description: intérêt.description ?? null,
          emoji: intérêt.emoji,
        })),
      })),
      domainesProfessionnels: référentielDonnéesHttp.categoriesDomaineAvecLeursDomaines.map((domaineProfessionnel) => ({
        id: domaineProfessionnel.categorieDomaine.id,
        nom: domaineProfessionnel.categorieDomaine.nom,
        emoji: domaineProfessionnel.categorieDomaine.emoji,
        sousCatégoriesdomainesProfessionnels: trierTableauDObjetsParOrdreAlphabétique(
          domaineProfessionnel.domaines,
          "nom",
        ).map((domaine) => ({
          id: domaine.id,
          nom: domaine.nom,
          description: domaine.description ?? null,
          emoji: domaine.emoji,
        })),
      })),
    };

    return {
      ...référentielDonnées,
      centresIntérêts: trierTableauDObjetsParOrdreAlphabétique(référentielDonnées.centresIntérêts, "nom"),
      domainesProfessionnels: trierTableauDObjetsParOrdreAlphabétique(référentielDonnées.domainesProfessionnels, "nom"),
    };
  }
}
