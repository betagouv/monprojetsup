/* eslint-disable sonarjs/cognitive-complexity */
import { type HttpClientOptions, type IHttpClient } from "./httpClient.interface";
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
import axios, { AxiosError } from "axios";

export class HttpClient implements IHttpClient {
  public récupérer = async <O extends object>(options: HttpClientOptions): Promise<O | Error> => {
    const { endpoint, méthode, body, contentType, headers } = options;

    try {
      const réponse = await axios({
        url: endpoint,
        method: méthode,
        data: body,
        headers: { "Content-Type": contentType ?? "application/json", ...headers },
      });

      if (réponse.status === 204) return {} as O;

      return réponse.data as O;
    } catch (error) {
      if (error instanceof AxiosError || (error instanceof Object && error.constructor.name === "AxiosError")) {
        const erreur = error as AxiosError;

        if (erreur.response) {
          const statusErreur = erreur.response?.status ?? 0;

          if (statusErreur === 400) {
            return new RequêteInvalideErreurHttp(options);
          }

          if (statusErreur === 401) {
            return new NonIdentifiéErreurHttp(options);
          }

          if (statusErreur === 403) {
            return new NonAutoriséErreurHttp(options);
          }

          if (statusErreur === 404) {
            return new RessourceNonTrouvéeErreurHttp(options);
          }

          if (statusErreur === 503) {
            return new ServeurTemporairementIndisponibleErreurHttp(options);
          }

          if (statusErreur === 500) {
            return new ErreurInterneServeurErreurHttp({ ...options, erreur: JSON.stringify(error) });
          }

          return new CodeRéponseInattenduErreurHttp({ ...options, erreur: JSON.stringify(error) }, statusErreur);
        }

        if (erreur.code === "ECONNABORTED")
          return new RequêteAnnuléeErreurHttp({ ...options, erreur: JSON.stringify(error) });

        if (erreur.code === "ERR_NETWORK" || erreur.cause?.message === "Network Error")
          return new ErreurRéseauErreurHttp({ ...options, erreur: JSON.stringify(error) });
      }

      return new ErreurInconnueErreurHttp({ ...options, erreur: JSON.stringify(error) });
    }
  };
}
