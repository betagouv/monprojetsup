import { AjoutTraceRéponseHTTP, type BodyAjoutTraceHTTP } from "./traceHttpService.interface";
import { type IMpsApiHttpClient } from "@/services/mpsApiHttpClient/mpsApiHttpClient.interface";
import { type TraceService } from "@/services/trace/trace.interface";
import { type components } from "@/types/api-mps";

export type ActionÉlève = NonNullable<components["schemas"]["TraceDTO"]["action"]>;

export type Trace = {
  action: ActionÉlève;
  param1: string | null;
  param2: string | null;
};

export class TraceHttpService implements TraceService {
  private readonly _ENDPOINT = "/api/v1/trace" as const;

  public constructor(private readonly _mpsApiHttpClient: IMpsApiHttpClient) {}

  public async ajouterTraceFicheFormation(id: string) {
    const trace: BodyAjoutTraceHTTP = {
      action: "fiche_formation",
      param1: id,
      param2: undefined,
    };
    await this.ajouterTrace(trace);
  }

  public async ajouterTraceFicheMetier(id: string) {
    const trace: BodyAjoutTraceHTTP = {
      action: "fiche_metier",
      param1: id,
      param2: undefined,
    };
    await this.ajouterTrace(trace);
  }

  public async ajouterTraceRechercheFormation(mots: string) {
    const trace: BodyAjoutTraceHTTP = {
      action: "recherche_formation",
      param1: mots,
      param2: undefined,
    };
    await this.ajouterTrace(trace);
  }

  public async ajouterTraceSuggestions() {
    const trace: BodyAjoutTraceHTTP = {
      action: "suggestions",
      param1: undefined,
      param2: undefined,
    };
    await this.ajouterTrace(trace);
  }

  public async ajouterTraceOngletFicheFormation(id: string, idOnglet: string) {
    const trace: BodyAjoutTraceHTTP = {
      action: "onglet_fiche_formation",
      param1: id,
      param2: idOnglet,
    };
    await this.ajouterTrace(trace);
  }

  public async ajouterTraceLienExterne(id: string, url: string) {
    const trace: BodyAjoutTraceHTTP = {
      action: "lien_externe",
      param1: id,
      param2: url,
    };
    await this.ajouterTrace(trace);
  }

  public async ajouterTrace(trace: BodyAjoutTraceHTTP): Promise<void | Error> {
    await this._mpsApiHttpClient.post<AjoutTraceRéponseHTTP>(this._ENDPOINT, trace);
  }
}
