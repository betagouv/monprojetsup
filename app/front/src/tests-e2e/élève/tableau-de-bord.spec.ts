import { i18n } from "@/configuration/i18n/i18n";
import { ÉlèveTestHelper } from "@/tests-e2e/élève/helpers/ÉlèveTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ÉlèveTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public naviguerVersLaPage = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto("/");
  };

  public naviguerVersLaPageSuiteAssociationPSOk = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto("/?associationPS=ok");
  };

  public naviguerVersLaPageSuiteAssociationPSErreur = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto("/?associationPS=erreur");
  };
}

test.describe("Page Tableau de bord Élève", () => {
  test("J'ai accès à 3 liens me permettant de naviguer vers les formations, mes favoris, et l'édition de mon profil", async ({
    page,
  }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPage();

    // THEN
    await expect(testhelper.lien(i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.TITRE)).toBeVisible();
    await expect(testhelper.lien(i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.TITRE)).toBeVisible();
    await expect(testhelper.lien(i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.TITRE)).toBeVisible();
    await expect(testhelper.lien(i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.TITRE)).toHaveAttribute(
      "href",
      testhelper.PAGE_FORMATIONS,
    );
    await expect(testhelper.lien(i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.TITRE)).toHaveAttribute(
      "href",
      testhelper.PAGE_FAVORIS,
    );
    await expect(testhelper.lien(i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.TITRE)).toHaveAttribute(
      "href",
      testhelper.PAGE_PROFIL,
    );
  });

  test.describe("Si j'arrive sur la page avec ?associationPS=ok dans l'url", () => {
    test("Je vois un message toast m'indiquant que l'association de mon compte avec PS s'est bien déroulée", async ({
      page,
    }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageSuiteAssociationPSOk();

      // THEN
      await expect(testhelper.toast()).toContainText(i18n.ÉLÈVE.TABLEAU_DE_BORD.TOAST_PARCOURSUP.SUCCÈS.TITRE);
    });
  });

  test.describe("Si j'arrive sur la page avec ?associationPS=erreur dans l'url", () => {
    test("Je vois un message toast m'indiquant que l'association de mon compte avec PS a échouée", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageSuiteAssociationPSErreur();

      // THEN
      await expect(testhelper.toast()).toContainText(i18n.ÉLÈVE.TABLEAU_DE_BORD.TOAST_PARCOURSUP.ERREUR.TITRE);
    });
  });
});
