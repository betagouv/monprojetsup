import { type Élève } from "@/features/élève/domain/élève.interface";
import { type Page } from "@playwright/test";

export class TestHelper {
  public constructor(protected _page: Page) {}

  protected initialiserProfilÉlèveParDéfaut = async (profilÉlève: Partial<Élève>) => {
    await this._page.context().addInitScript((argProfilÉlève) => {
      const élève: Élève = {
        situation: null,
        classe: null,
        bac: null,
        spécialités: null,
        domaines: null,
        centresIntêrets: null,
        métiersFavoris: null,
        duréeÉtudesPrévue: null,
        alternance: null,
        communesFavorites: null,
        formationsFavorites: null,
        formationsMasquées: null,
        ...argProfilÉlève,
      };

      window.sessionStorage.setItem("élève", JSON.stringify(élève));
    }, profilÉlève);
  };

  public seConnecterCommeÉlèveAvecParcoursInscriptionTerminé = async () => {
    const profilÉlève: Élève = {
      situation: "quelques_pistes",
      classe: "premiere",
      bac: "Générale",
      spécialités: [],
      domaines: ["T_ITM_1534"],
      centresIntêrets: ["travail_manuel_bricoler"],
      métiersFavoris: [],
      alternance: "indifferent",
      communesFavorites: [],
      duréeÉtudesPrévue: "courte",
      formationsFavorites: [],
      formationsMasquées: [],
    };

    await this.initialiserProfilÉlèveParDéfaut(profilÉlève);
  };
}
