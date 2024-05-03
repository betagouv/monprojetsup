import { type HttpClientOptions, type IHttpClient } from "./httpClient.interface";
import { type ILogger } from "@/services/logger/logger.interface";

export class HttpClient implements IHttpClient {
  public constructor(private readonly _logger: ILogger) {}

  public fetch = async <O extends {}>(options: HttpClientOptions): Promise<O | undefined> => {
    const { endpoint, method, body, contentType, headers } = options;

    try {
      const response = await fetch(endpoint, {
        method,
        body: JSON.stringify(body),
        headers: {
          "content-type": contentType ?? "application/json",
          ...headers,
        },
      });

      if (!response?.ok) {
        this._logger.error({ endpoint, method, body, status: response.status });
        return undefined;
      }

      return (await response.json()) as O;
    } catch (error) {
      this._logger.error({ endpoint, method, body, error });
      return undefined;
    }
  };
}
