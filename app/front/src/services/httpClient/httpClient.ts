import { type HttpClientOptions, type IHttpClient } from "./httpClient.interface";
import { type ILogger } from "@/services/logger/logger.interface";

export class HttpClient implements IHttpClient {
  public constructor(private readonly _logger: ILogger) {}

  public récupérer = async <O extends {}>(options: HttpClientOptions): Promise<O | undefined> => {
    const { endpoint, méthode, body, contentType, headers } = options;

    try {
      const response = await fetch(endpoint, {
        method: méthode,
        body: JSON.stringify(body),
        headers: {
          "content-type": contentType ?? "application/json",
          ...headers,
        },
      });

      if (!response?.ok) {
        this._logger.error({ endpoint, méthode, body, status: response.status });
        return undefined;
      }

      if (response.status === 204) return {} as O;
      return (await response.json()) as O;
    } catch (error) {
      this._logger.error({ endpoint, méthode, body, error });
      return undefined;
    }
  };
}
