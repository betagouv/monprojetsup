import { i18n } from "@/configuration/i18n/i18n";
import { ÉlèveTestHelper } from "@/tests-e2e/élève/helpers/ÉlèveTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ÉlèveTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public naviguerVersLaPage = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto("/eleve/inscription/confirmation");
  };
}

test.describe("Inscription élève - Confirmation", () => {
  test("J'ai un bouton qui me permet d'accéder à mon tableau de bord", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await page.getByRole("link", { name: i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.BOUTON_ACTION }).click();
    await page.waitForURL("/");

    // THEN
    expect(page.url()).toMatch("/");
  });
});
