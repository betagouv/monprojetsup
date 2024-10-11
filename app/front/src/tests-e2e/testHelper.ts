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
        centresIntérêts: null,
        métiersFavoris: null,
        duréeÉtudesPrévue: null,
        alternance: null,
        moyenneGénérale: null,
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
      classe: "terminale",
      bac: "Générale",
      spécialités: [],
      domaines: ["T_ITM_1534"],
      centresIntérêts: ["travail_manuel_bricoler"],
      métiersFavoris: [],
      alternance: "indifferent",
      communesFavorites: [],
      moyenneGénérale: 15,
      duréeÉtudesPrévue: "courte",
      formationsFavorites: [],
      formationsMasquées: [],
    };

    await this.initialiserProfilÉlèveParDéfaut(profilÉlève);
  };
}
