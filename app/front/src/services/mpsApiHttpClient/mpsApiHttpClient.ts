import { type IMpsApiHttpClient } from "./mpsApiHttpClient.interface";
import { env } from "@/configuration/environnement";
import { type IHttpClient } from "@/services/httpClient/httpClient.interface";
import { type paths } from "@/types/api-mps";

export class MpsApiHttpClient implements IMpsApiHttpClient {
  public constructor(
    private readonly _httpClient: IHttpClient,
    private readonly _apiBaseUrl: string,
  ) {}

  public get = async <O extends {}>(endpoint: keyof paths): Promise<O | undefined> => {
    return await this._httpClient.récupérer<O>({
      endpoint: `${this._apiBaseUrl}${endpoint}`,
      méthode: "GET",
      headers: {
        authorization: `Bearer ${this._récupérerJWT()}`,
      },
    });
  };

  public post = async <O extends {}>(endpoint: keyof paths, body: {}): Promise<O | undefined> => {
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
      `oidc.user:${env.VITE_KEYCLOAK_ROYAUME_URL}:${env.VITE_KEYCLOAK_CLIENT_ID}`,
    );

    if (!sessionStorageOIDC) return "";

    return JSON.parse(sessionStorageOIDC).access_token ?? "";
  };
}
