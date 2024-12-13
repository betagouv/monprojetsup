import { i18n } from "@/configuration/i18n/i18n";
import { ListeEtAperçuTestHelper } from "@/tests-e2e/élève/helpers/ListeEtAperçuTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ListeEtAperçuTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }
}

test.describe("Formations - Sélectionner", () => {
  test("Je peux ajouter une formation en favori", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonAjouterEnFavori();

    // THEN
    await expect(page.getByText(i18n.COMMUN.AJOUTÉ_À_MA_SÉLECTION)).toBeVisible();
    expect(await testhelper.contenuDeLaPremièreCarteFormation()).toContain(i18n.ACCESSIBILITÉ.FAVORI);
  });

  test("Je peux supprimer une formation de mes favoris", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonAjouterEnFavori();
    await testhelper.appuyerSurLeBoutonSupprimerFavori();

    // THEN
    await expect(testhelper.boutonAjouterEnFavori()).toBeVisible();
    expect(await testhelper.contenuDeLaPremièreCarteFormation()).not.toContain(i18n.ACCESSIBILITÉ.FAVORI);
  });

  test.describe("Si j'avais déjà des formations favorites", () => {
    test("Je vois bien mes formations favorites", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageFormationsAvecDesFavoris();

      // THEN
      await expect(page.getByText(i18n.COMMUN.AJOUTÉ_À_MA_SÉLECTION)).toBeVisible();
      await expect(testhelper.cartesFormations().getByText(i18n.ACCESSIBILITÉ.FAVORI)).toHaveCount(
        testhelper.NOMBRE_FORMATIONS_FAVORITES,
      );
    });

    test("Je peux supprimer une formation de mes favoris", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageFormationsAvecDesFavoris();
      await testhelper.appuyerSurLeBoutonSupprimerFavori();

      // THEN
      await expect(testhelper.boutonAjouterEnFavori()).toBeVisible();
      expect(await testhelper.contenuDeLaPremièreCarteFormation()).not.toContain(i18n.ACCESSIBILITÉ.FAVORI);
    });
  });
});
