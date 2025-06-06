import { i18n } from "@/configuration/i18n/i18n";
import { InscriptionTestHelper } from "@/tests-e2e/élève/helpers/InscriptionTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public CENTRE_INTÉRÊT_VOYAGER = "Voyager";

  public CENTRE_INTÉRÊT_BRICOLER = "Bricoler";

  public CATÉGORIE_DÉCOUVRIR_MONDE = "Découvrir le monde";

  public BOUTON_APPUYÉ = "aria-pressed";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/interets", "/eleve/inscription/metiers", {
      situation: "quelques_pistes",
      classe: "premiere",
      bac: "Générale",
      spécialités: [],
      domaines: ["T_ITM_1534"],
    });
  }

  public cliquerSurUneCatégorie = async (catégorieLabel: string) => {
    await this.boutonCatégorie(catégorieLabel).click();
  };

  public cliquerSurUnCentreIntérêt = async (nomDuCentreIntérêt: string) => {
    await this.boutonCentreIntérêt(nomDuCentreIntérêt).click();
  };

  public boutonCatégorie = (catégorieLabel: string) => {
    return this._page.getByRole("button", { name: catégorieLabel, exact: true });
  };

  public boutonCentreIntérêt = (nomDuCentreIntérêt: string) => {
    return this._page.getByRole("button", { name: nomDuCentreIntérêt, exact: true });
  };
}

test.describe("Inscription élève - Mes centres intérêts", () => {
  test("L'élève doit choisir au moins un centre d'intérêt", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.toast()).toContainText(i18n.ÉLÈVE.INTÉRÊTS.SÉLECTIONNE_AU_MOINS_UN);
  });

  test("Je peux sélectionner des centres intérêts", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
    await testhelper.cliquerSurUnCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER);

    // THEN
    expect(
      await testhelper.boutonCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER).getAttribute(testhelper.BOUTON_APPUYÉ),
    ).toBe("true");
  });

  test("Je peux supprimer des centres intérêts sélectionnés", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
    await testhelper.cliquerSurUnCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER);
    await testhelper.cliquerSurUnCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER);

    // THEN
    expect(
      await testhelper.boutonCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER).getAttribute(testhelper.BOUTON_APPUYÉ),
    ).toBe("false");
  });

  test("Si au moins un centre d'intérêt est renseigné, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
    await testhelper.cliquerSurUnCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER);
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
      await testhelper.cliquerSurUnCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER);
      await testhelper.cliquerSurUneCatégorie("Travailler de mes mains");
      await testhelper.cliquerSurUnCentreIntérêt(testhelper.CENTRE_INTÉRÊT_BRICOLER);
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.boutonCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER)).toBeVisible();
      await expect(testhelper.boutonCentreIntérêt(testhelper.CENTRE_INTÉRÊT_BRICOLER)).toBeVisible();
      expect(
        await testhelper.boutonCentreIntérêt(testhelper.CENTRE_INTÉRÊT_VOYAGER).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
      expect(
        await testhelper.boutonCentreIntérêt(testhelper.CENTRE_INTÉRÊT_BRICOLER).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
    });
  });
});
