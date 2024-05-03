import { i18n } from "../../src/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class TestHelper {
  public DOMAINE_ART = "Art";

  public DOMAINE_SCIENCE_LANGAGE = "Science du langage";

  public CATÉGORIE_ARTS_CULTURE = "Arts et Culture";

  public BOUTON_APPUYÉ = "aria-pressed";

  public constructor(private _page: Page) {}

  public readonly urlPageSuivante = "/inscription/interets";

  public naviguerVersLaPage = async () => {
    await this._page.goto("/inscription/domaines");
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

  public cliquerSurUnDomaine = async (nomDuDomaine: string) => {
    await this.boutonDomaine(nomDuDomaine).click();
  };

  public boutonCatégorie = (catégorieLabel: string) => {
    return this._page.getByRole("button", { name: catégorieLabel, exact: true });
  };

  public boutonDomaine = (nomDuDomaine: string) => {
    return this._page.getByRole("button", { name: nomDuDomaine, exact: true });
  };

  private _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.CONTINUER });
  };

  private _boutonRetour = () => {
    return this._page.getByRole("link", { name: i18n.COMMUN.RETOUR });
  };
}

test.describe("Inscription élève - Mes domaines", () => {
  test("Je peux sélectionner des domaines", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_ART);

    // THEN
    expect(await testhelper.boutonDomaine(testhelper.DOMAINE_ART).getAttribute(testhelper.BOUTON_APPUYÉ)).toBe("true");
  });

  test("Je peux supprimer des domaines sélectionnés", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_ART);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_ART);

    // THEN
    expect(await testhelper.boutonDomaine(testhelper.DOMAINE_ART).getAttribute(testhelper.BOUTON_APPUYÉ)).toBe("false");
  });

  test("Je peux passer à l'étape suivante sans rien renseigner", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
      await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_ART);
      await testhelper.cliquerSurUneCatégorie("Education et Formation");
      await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_SCIENCE_LANGAGE);
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      expect(testhelper.boutonDomaine(testhelper.DOMAINE_ART)).toBeVisible();
      expect(testhelper.boutonDomaine(testhelper.DOMAINE_SCIENCE_LANGAGE)).toBeVisible();
      expect(await testhelper.boutonDomaine(testhelper.DOMAINE_ART).getAttribute(testhelper.BOUTON_APPUYÉ)).toBe(
        "true",
      );
      expect(
        await testhelper.boutonDomaine(testhelper.DOMAINE_SCIENCE_LANGAGE).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
    });
  });
});
