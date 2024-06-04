import { i18n } from "../../../src/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class TestHelper {
  public CENTRE_INTÊRET_VOYAGER = "Voyager";

  public CENTRE_INTÊRET_BRICOLER = "Bricoler";

  public CATÉGORIE_DÉCOUVRIR_MONDE = "Découvrir le monde";

  public BOUTON_APPUYÉ = "aria-pressed";

  public constructor(private _page: Page) {}

  public readonly urlPageSuivante = "/eleve/inscription/metiers";

  public naviguerVersLaPage = async () => {
    await this._page.goto("/eleve/inscription/interets");
  };

  public soumettreLeFormulaire = async () => {
    await this._boutonSoumissionFormulaire().click();
  };

  public revenirÀLÉtapePrécédente = async () => {
    await this._boutonRetour().click();
  };

  public cliquerSurUneCatégorie = async (catégorieLabel: string) => {
    await this.boutonCatégorie(catégorieLabel).click();
  };

  public cliquerSurUnCentreIntêret = async (nomDuCentreIntêret: string) => {
    await this.boutonCentreIntêret(nomDuCentreIntêret).click();
  };

  public boutonCatégorie = (catégorieLabel: string) => {
    return this._page.getByRole("button", { name: catégorieLabel, exact: true });
  };

  public boutonCentreIntêret = (nomDuCentreIntêret: string) => {
    return this._page.getByRole("button", { name: nomDuCentreIntêret, exact: true });
  };

  public messageErreurAuMoinsUnIntêret = () => {
    return this._page.locator("#intêrets-message").getByText(i18n.ÉLÈVE.INTÊRETS.SÉLECTIONNE_AU_MOINS_UN);
  };

  private _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.CONTINUER });
  };

  private _boutonRetour = () => {
    return this._page.getByRole("link", { name: i18n.COMMUN.RETOUR });
  };
}

test.describe("Inscription élève - Mes centres intêrets", () => {
  test("L'élève doit choisir au moins un centre d'intêret", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurAuMoinsUnIntêret()).toBeVisible();
  });

  test("Je peux sélectionner des centres intêrets", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
    await testhelper.cliquerSurUnCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER);

    // THEN
    expect(
      await testhelper.boutonCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER).getAttribute(testhelper.BOUTON_APPUYÉ),
    ).toBe("true");
  });

  test("Je peux supprimer des centres intêrets sélectionnés", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
    await testhelper.cliquerSurUnCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER);
    await testhelper.cliquerSurUnCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER);

    // THEN
    expect(
      await testhelper.boutonCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER).getAttribute(testhelper.BOUTON_APPUYÉ),
    ).toBe("false");
  });

  test("Si au moins un centre d'intêret est renseigné, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
    await testhelper.cliquerSurUnCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER);
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_DÉCOUVRIR_MONDE);
      await testhelper.cliquerSurUnCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER);
      await testhelper.cliquerSurUneCatégorie("Travailler de mes mains");
      await testhelper.cliquerSurUnCentreIntêret(testhelper.CENTRE_INTÊRET_BRICOLER);
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      expect(testhelper.boutonCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER)).toBeVisible();
      expect(testhelper.boutonCentreIntêret(testhelper.CENTRE_INTÊRET_BRICOLER)).toBeVisible();
      expect(
        await testhelper.boutonCentreIntêret(testhelper.CENTRE_INTÊRET_VOYAGER).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
      expect(
        await testhelper.boutonCentreIntêret(testhelper.CENTRE_INTÊRET_BRICOLER).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
    });
  });
});
