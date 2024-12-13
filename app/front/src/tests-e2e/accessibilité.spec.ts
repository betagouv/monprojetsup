import { TestHelper } from "./testHelper";
import { Paths } from "@/types/commons";
import Axe from "@axe-core/playwright";
import { expect, type Page, test } from "@playwright/test";

const PAGES_PATH: Array<Paths> = [
  "/",
  "/profil",
  "/eleve/inscription/projet",
  "/eleve/inscription/scolarite",
  "/eleve/inscription/domaines",
  "/eleve/inscription/interets",
  "/eleve/inscription/metiers",
  "/eleve/inscription/etude",
  "/eleve/inscription/formations",
  "/eleve/inscription/confirmation",
  "/formations",
  "/favoris",
  "/cookies",
  "/declaration-accessiblite",
  "/plan-du-site",
];

class Test extends TestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public naviguerVersLaPage = async (path: Paths) => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto(path, { waitUntil: "networkidle" });
    await new Promise((resolve) => {
      setTimeout(resolve, 500);
    });
  };
}

for (const pagePath of PAGES_PATH) {
  test(`la page "${pagePath}" valide tous les tests d'accessibilité`, async ({ page }) => {
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage(pagePath);

    const accessibilityScanResults = await new Axe({ page })
      .exclude(".fr-footer__partners-title")
      .exclude("#header-menu-modal-fr-header")
      .analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });
}
