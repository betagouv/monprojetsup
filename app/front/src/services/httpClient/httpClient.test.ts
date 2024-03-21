import { HttpClient } from "./httpClient";
import { type HttpClientOptions } from "./httpClient.interface";
import { type ILogger } from "@/services/logger/logger.interface";
import { mock } from "vitest-mock-extended";

describe("HttpClient", () => {
  let httpClient: HttpClient;
  const logger = mock<ILogger>();
  const ENDPOINT = "http://example.com/api";

  beforeEach(() => {
    httpClient = new HttpClient(logger);
  });

  describe("fetch", () => {
    test("doit renvoyer les données si tout s'est bien déroulé", async () => {
      // GIVEN
      const options: HttpClientOptions = {
        endpoint: ENDPOINT,
        method: "GET",
      };
      const responseData = { id: 1, name: "John Doe" };
      const response = new Response(JSON.stringify(responseData), { status: 200 });
      vitest.spyOn(global, "fetch").mockResolvedValueOnce(response);

      // WHEN
      const result = await httpClient.fetch(options);

      // THEN
      expect(result).toEqual(responseData);
      expect(global.fetch).toHaveBeenCalledWith(options.endpoint, {
        method: options.method,
        body: undefined,
        headers: {
          "content-type": "application/json",
          authorization: "",
        },
      });
      expect(logger.error).not.toHaveBeenCalled();
    });

    test("doit renvoyer undefined et logger l'erreur si la requête a échouée (status != ok)", async () => {
      // GIVEN
      const options: HttpClientOptions = {
        endpoint: ENDPOINT,
        method: "GET",
      };
      const response = new Response(null, { status: 404 });
      vitest.spyOn(global, "fetch").mockResolvedValueOnce(response);

      // WHEN
      const result = await httpClient.fetch(options);

      // THEN
      expect(result).toBeUndefined();
      expect(global.fetch).toHaveBeenCalledWith(options.endpoint, {
        method: options.method,
        body: undefined,
        headers: {
          "content-type": "application/json",
          authorization: "",
        },
      });
      expect(logger.error).toHaveBeenCalledWith({
        endpoint: options.endpoint,
        method: options.method,
        body: undefined,
        status: response.status,
      });
    });

    test("doit renvoyer undefined et logger l'erreur si une erreur survient", async () => {
      // GIVEN
      const options: HttpClientOptions = {
        endpoint: ENDPOINT,
        method: "GET",
      };
      const error = new Error("Network error");
      vitest.spyOn(global, "fetch").mockRejectedValueOnce(error);

      // WHEN
      const result = await httpClient.fetch(options);

      // THEN
      expect(result).toBeUndefined();
      expect(global.fetch).toHaveBeenCalledWith(options.endpoint, {
        method: options.method,
        body: undefined,
        headers: {
          "content-type": "application/json",
          authorization: "",
        },
      });
      expect(logger.error).toHaveBeenCalledWith({
        endpoint: options.endpoint,
        method: options.method,
        body: undefined,
        error,
      });
    });
  });
});
