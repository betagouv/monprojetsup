import { type RécupérerRéférentielDonnéesRéponseHTTP } from "./référentielDonnéesHttpRepository.interface";
import { type RéférentielDonnées } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { type RéférentielDonnéesRepository } from "@/features/référentielDonnées/infrastructure/référentielDonnéesRepository.interface";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

export class RéférentielDonnéesHttpRepository implements RéférentielDonnéesRepository {
  private _ENDPOINT = "/api/v1/referentiel" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérer(): Promise<RéférentielDonnées | undefined> {
    const réponse = await this._mpsApiHttpClient.get<RécupérerRéférentielDonnéesRéponseHTTP>(this._ENDPOINT);

    if (!réponse) return undefined;

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
      bacs: référentielDonnéesHttp.baccalaureatsAvecLeurSpecialites.map((bac) => ({
        id: bac.baccalaureat.id,
        nom: bac.baccalaureat.nom,
        spécialités: bac.specialites,
      })),
      centresIntêrets: référentielDonnéesHttp.categoriesDInteretsAvecLeursSousCategories.map((centreIntêret) => ({
        id: centreIntêret.categorieInteret.id,
        nom: centreIntêret.categorieInteret.nom,
        emoji: centreIntêret.categorieInteret.emoji,
        sousCatégoriesCentreIntêret: this._trierTableauDObjetsParOrdreAlphabétique(
          centreIntêret.sousCategoriesInterets,
          "nom",
        ),
      })),
      domainesProfessionnels: référentielDonnéesHttp.categoriesDomaineAvecLeursDomaines.map((domaineProfessionnel) => ({
        id: domaineProfessionnel.categorieDomaine.id,
        nom: domaineProfessionnel.categorieDomaine.nom,
        emoji: domaineProfessionnel.categorieDomaine.emoji,
        sousCatégoriesdomainesProfessionnels: this._trierTableauDObjetsParOrdreAlphabétique(
          domaineProfessionnel.domaines,
          "nom",
        ),
      })),
    };

    return {
      ...référentielDonnées,
      centresIntêrets: this._trierTableauDObjetsParOrdreAlphabétique(référentielDonnées.centresIntêrets, "nom"),
      domainesProfessionnels: this._trierTableauDObjetsParOrdreAlphabétique(
        référentielDonnées.domainesProfessionnels,
        "nom",
      ),
    };
  }

  private _trierTableauDObjetsParOrdreAlphabétique<O>(tableau: O[], propriété: keyof O): O[] {
    return [...tableau].sort((a, b) => {
      if (a[propriété] < b[propriété]) {
        return -1;
      }

      if (a[propriété] > b[propriété]) {
        return 1;
      }

      return 0;
    });
  }
}
