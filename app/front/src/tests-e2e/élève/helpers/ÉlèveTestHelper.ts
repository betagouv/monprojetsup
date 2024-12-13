import { GlobalTestHelper } from "./GlobalTestHelper";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { type Page } from "@playwright/test";

export class ÉlèveTestHelper extends GlobalTestHelper {
  public NOM_UTILISATEUR = "nina élève";

  public constructor(protected _page: Page) {
    super(_page);
  }

  protected initialiserProfilÉlèveParDéfaut = async (profilÉlève: Partial<Élève>) => {
    await this._page.context().addInitScript((argumentsProfilÉlève) => {
      const élève: Élève = {
        compteParcoursupAssocié: false,
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
        formations: null,
        voeuxFavoris: null,
        formationsMasquées: null,
        ambitions: null,
        notesPersonnelles: null,
        ...argumentsProfilÉlève,
      };

      window.sessionStorage.setItem("élève", JSON.stringify(élève));
    }, profilÉlève);
  };

  public seConnecterCommeÉlèveAvecParcoursInscriptionTerminé = async (profilÉlève?: Partial<Élève>) => {
    const profilÉlèveParDéfaut: Élève = {
      compteParcoursupAssocié: false,
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
      formations: [],
      voeuxFavoris: [],
      formationsMasquées: [],
      ambitions: [],
      notesPersonnelles: [],
    };

    await this.initialiserProfilÉlèveParDéfaut({ ...profilÉlèveParDéfaut, ...profilÉlève });
  };
}
