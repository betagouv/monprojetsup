import { ListeEtAperçuTestHelper } from "@/tests-e2e/élève/helpers/ListeEtAperçuTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ListeEtAperçuTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }
}

test.describe("Page Formations Élève", () => {
  test("Je peux voir les formations qui me sont suggérées", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();

    // THEN
    await expect(testhelper.cartesFormations()).toHaveCount(5);
  });

  test("Par défaut je vois la fiche détaillée de la première formation suggérée", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const premièreFormationSuggérée = (await Test.récupérerFormationsSuggérées()).at(0);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();

    // THEN
    expect(await testhelper.contenuDeLaPremièreCarteFormation()).toContain(premièreFormationSuggérée?.nom);
    expect(await testhelper.titrePage()).toBe(premièreFormationSuggérée?.nom);
    expect(page.url()).toContain(`${testhelper.PAGE_FORMATIONS}#${premièreFormationSuggérée?.id}`);
  });

  test("Je peux changer de fiche formation en cliquant sur une carte", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const dernièreFormationSuggérée = (await Test.récupérerFormationsSuggérées()).at(-1);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.cliquerSurLaDernièreCarteFormation();

    // THEN
    expect(await testhelper.contenuDeLaDernièreCarteFormation()).toContain(dernièreFormationSuggérée?.nom);
    expect(await testhelper.titrePage()).toBe(dernièreFormationSuggérée?.nom);
    expect(page.url()).toContain(`${testhelper.PAGE_FORMATIONS}#${dernièreFormationSuggérée?.id}`);
  });
});
