import { type IMpsApiHttpClient } from "./mpsApiHttpClient.interface";
import { environnement } from "@/configuration/environnement";
import { type IHttpClient } from "@/services/httpClient/httpClient.interface";
import { type paths } from "@/types/api-mps";

export class MpsApiHttpClient implements IMpsApiHttpClient {
  public constructor(
    private readonly _httpClient: IHttpClient,
    private readonly _apiBaseUrl: string,
  ) {}

  public get = async <O extends object>(
    endpoint: keyof paths,
    paramètresDeRequête?: URLSearchParams,
  ): Promise<O | Error> => {
    return await this._httpClient.récupérer<O>({
      endpoint: paramètresDeRequête
        ? `${this._apiBaseUrl}${endpoint}?${paramètresDeRequête.toString()}`
        : `${this._apiBaseUrl}${endpoint}`,
      méthode: "GET",
      headers: {
        authorization: `Bearer ${this._récupérerJWT()}`,
      },
    });
  };

  public post = async <O extends object>(endpoint: keyof paths, body: object): Promise<O | Error> => {
    return await this._httpClient.récupérer<O>({
      endpoint: `${this._apiBaseUrl}${endpoint}`,
      méthode: "POST",
      body,
      headers: {
        authorization: `Bearer ${this._récupérerJWT()}`,
      },
    });
  };

  private _récupérerJWT = (): string => {
    const sessionStorageOIDC = sessionStorage.getItem(
      `oidc.user:${environnement.VITE_KEYCLOAK_URL}/realms/${environnement.VITE_KEYCLOAK_ROYAUME}:${environnement.VITE_KEYCLOAK_CLIENT_ID}`,
    );

    if (!sessionStorageOIDC) return "";

    const sessionStorageParsé = JSON.parse(sessionStorageOIDC) as unknown as { access_token: string | null };

    return sessionStorageParsé.access_token ?? "";
  };
}
