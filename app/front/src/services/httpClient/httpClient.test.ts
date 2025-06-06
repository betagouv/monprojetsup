import { HttpClient } from "./httpClient";
import { type HttpClientOptions, IHttpClient } from "./httpClient.interface";
import {
  CodeRéponseInattenduErreurHttp,
  ErreurInconnueErreurHttp,
  ErreurInterneServeurErreurHttp,
  ErreurRéseauErreurHttp,
  NonAutoriséErreurHttp,
  NonIdentifiéErreurHttp,
  RequêteAnnuléeErreurHttp,
  RequêteInvalideErreurHttp,
  RessourceNonTrouvéeErreurHttp,
  ServeurTemporairementIndisponibleErreurHttp,
} from "@/services/erreurs/erreursHttp";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

vitest.mock("@/configuration/dépendances/dépendances", () => ({
  dépendances: {
    logger: {
      consigner: vitest.fn(),
    },
  },
}));

const mockAxios = new AxiosMockAdapter(axios);

describe("HttpClient", () => {
  let httpClient: IHttpClient;
  const ENDPOINT = "http://example.com/api";
  const options: HttpClientOptions = {
    endpoint: ENDPOINT,
    méthode: "GET",
  };
  const argumentsAppelGetAxios = {
    url: options.endpoint,
    method: options.méthode.toLocaleLowerCase(),
    data: undefined,
    headers: {
      "Content-Type": "application/json",
    },
  };

  beforeEach(() => {
    httpClient = new HttpClient();
  });

  describe("récupérer", () => {
    beforeEach(() => {
      mockAxios.reset();
    });

    test("doit renvoyer les données si tout s'est bien déroulé", async () => {
      // GIVEN
      const donnéesRéponse = { id: 1, name: "John Doe" };
      mockAxios.onGet(ENDPOINT).replyOnce(200, donnéesRéponse);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toEqual(donnéesRéponse);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur RequêteInvalideErreurHttp si la requête a échouée avec un status 400", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(400);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(RequêteInvalideErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur NonIdentifiéErreurHttp si la requête a échouée avec un status 401", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(401);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(NonIdentifiéErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur NonAutoriséErreurHttp si la requête a échouée avec un status 403", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(403);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(NonAutoriséErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur RessourceNonTrouvéeErreurHttp si la requête a échouée avec un status 404", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(404);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(RessourceNonTrouvéeErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur ServeurTemporairementIndisponibleErreurHttp si la requête a échouée avec un status 503", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(503);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(ServeurTemporairementIndisponibleErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur ErreurInterneServeurErreurHttp si la requête a échouée avec un status 500", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(500);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(ErreurInterneServeurErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur CodeRéponseInattenduErreurHttp si la requête a échouée avec n'importe quel autre status !== ok non géré", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(410);

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(CodeRéponseInattenduErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur RequêteAnnuléeErreurHttp si la requête a été interrompue", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).abortRequestOnce();

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(RequêteAnnuléeErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur ErreurRéseauErreurHttp si la requête a rencontré un problème réseau", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).networkErrorOnce();

      // WHEN
      const réponse = await httpClient.récupérer(options);

      // THEN
      expect(réponse).toBeInstanceOf(ErreurRéseauErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });

    test("doit renvoyer une erreur générique si une erreur survient", async () => {
      // GIVEN
      mockAxios.onGet(ENDPOINT).replyOnce(() => {
        return Promise.reject(new Error("Erreur inconnue"));
      });

      // WHEN
      const réponse = await httpClient.récupérer(options);
      // THEN
      expect(réponse).toBeInstanceOf(ErreurInconnueErreurHttp);
      expect(mockAxios.history.get.length).toBe(1);
      expect(mockAxios.history.get[0]).toMatchObject(argumentsAppelGetAxios);
    });
  });
});
