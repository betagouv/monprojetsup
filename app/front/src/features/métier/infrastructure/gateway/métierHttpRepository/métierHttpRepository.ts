import {
  type RechercheSuccincteMétiersRéponseHTTP,
  type RécupérerMétiersRéponseHTTP,
} from "./métierHttpRepository.interface";
import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";
import { RessourceNonTrouvéeErreurHttp } from "@/services/erreurs/erreursHttp";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";

export class métierHttpRepository implements MétierRepository {
  private _ENDPOINT = "/api/v1/public/metiers" as const;

  public constructor(private _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async récupérer(métierId: string): Promise<Métier | Error> {
    const réponse = await this.récupérerPlusieurs([métierId]);

    if (réponse instanceof RessourceNonTrouvéeErreurHttp) {
      return new RessourceNonTrouvéeErreur();
    }

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse?.[0];
  }

  public async récupérerPlusieurs(métierIds: string[]): Promise<Métier[] | Error> {
    const paramètresDeRequête = new URLSearchParams();

    for (const métierId of métierIds) {
      paramètresDeRequête.append("ids", métierId);
    }

    const réponse = await this._mpsApiHttpClient.get<RécupérerMétiersRéponseHTTP>(this._ENDPOINT, paramètresDeRequête);

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.metiers.map((métier) => this._mapperVersLeDomaine(métier));
  }

  public async rechercher(recherche: string): Promise<Métier[] | Error> {
    const paramètresDeRequête = new URLSearchParams();
    paramètresDeRequête.set("recherche", recherche);

    const réponse = await this._mpsApiHttpClient.get<RechercheSuccincteMétiersRéponseHTTP>(
      `${this._ENDPOINT}/recherche/succincte`,
      paramètresDeRequête,
    );

    if (réponse instanceof Error) {
      return réponse;
    }

    return réponse.metiers.map((métier) =>
      this._mapperVersLeDomaine({
        ...métier,
        liens: [],
        formations: [],
      }),
    );
  }

  private _mapperVersLeDomaine(métierHttp: RécupérerMétiersRéponseHTTP["metiers"][number]): Métier {
    return {
      id: métierHttp.id,
      nom: métierHttp.nom,
      descriptif: métierHttp.descriptif ?? null,
      liens: métierHttp.liens.map((lien) => ({
        intitulé: lien.nom,
        url: lien.url,
      })),
      formations: métierHttp.formations,
    };
  }
}
