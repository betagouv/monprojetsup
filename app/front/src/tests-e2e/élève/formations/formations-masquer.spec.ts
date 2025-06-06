import { i18n } from "@/configuration/i18n/i18n";
import { ListeEtAperçuTestHelper } from "@/tests-e2e/élève/helpers/ListeEtAperçuTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ListeEtAperçuTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public appuyerSurLeBoutonMasquer = async () => {
    return this._boutonMasquer().click();
  };

  public appuyerSurLeBoutonNePlusMasquer = async () => {
    return this._boutonNePlusMasquer().click();
  };

  private _boutonMasquer = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.NE_PLUS_VOIR });
  };

  private _boutonNePlusMasquer = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.AFFICHER_À_NOUVEAU });
  };
}

test.describe("Formations - Masquer", () => {
  test("Je peux masquer une formation", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonMasquer();

    // THEN
    await expect(page.getByText(i18n.COMMUN.AFFICHER_À_NOUVEAU)).toBeVisible();
    expect(await testhelper.contenuDeLaPremièreCarteFormation()).toContain(i18n.ACCESSIBILITÉ.MASQUÉ);
  });

  test("Je peux afficher à nouveau une formation", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonMasquer();
    await testhelper.appuyerSurLeBoutonNePlusMasquer();

    // THEN
    await expect(page.getByText(i18n.COMMUN.NE_PLUS_VOIR)).toBeVisible();
    expect(await testhelper.contenuDeLaPremièreCarteFormation()).not.toContain(i18n.ACCESSIBILITÉ.MASQUÉ);
  });
});
