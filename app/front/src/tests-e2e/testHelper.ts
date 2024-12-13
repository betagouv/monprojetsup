import { type Élève } from "@/features/élève/domain/élève.interface";
import { type Page } from "@playwright/test";

export class TestHelper {
  public PAGE_FORMATIONS = "/formations";

  public PAGE_FAVORIS = "/favoris";

  public PAGE_TABLEAU_DE_BORD = "/";

  public PAGE_PROFIL = "/profil";

  public NOM_UTILISATEUR = "nina élève";

  public constructor(protected _page: Page) {}

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

  public seConnecterCommeÉlèveAvecParcoursInscriptionTerminé = async () => {
    const profilÉlève: Élève = {
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

    await this.initialiserProfilÉlèveParDéfaut(profilÉlève);
  };

  public toast = () => {
    return this._page.locator("#contenu").getByRole("status");
  };

  public lien = (nom: string) => {
    return this._page.getByRole("link", { name: nom });
  };
}
