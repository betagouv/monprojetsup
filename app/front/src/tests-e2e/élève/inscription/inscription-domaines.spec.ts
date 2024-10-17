import { InscriptionTestHelper } from "./inscriptionTestHelper";
import { i18n } from "@/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public DOMAINE_AUDIOVISUEL = "Audiovisuel";

  public DOMAINE_COMMERCE_VENTE = "Commerce - vente";

  public CATÉGORIE_COMMERCE = "Commerce";

  public CATÉGORIE_ARTS_CULTURE = "Arts et Culture";

  public BOUTON_APPUYÉ = "aria-pressed";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/domaines", "/eleve/inscription/interets", {
      situation: "quelques_pistes",
      classe: "premiere",
      bac: "Générale",
      spécialités: [],
    });
  }

  public cliquerSurUneCatégorie = async (catégorieLabel: string) => {
    await this.boutonCatégorie(catégorieLabel).click();
  };

  public cliquerSurUnDomaine = async (nomDuDomaine: string) => {
    await this.boutonDomaine(nomDuDomaine).click();
  };

  public boutonCatégorie = (catégorieLabel: string) => {
    return this._page.getByRole("button", { name: catégorieLabel });
  };

  public boutonDomaine = (nomDuDomaine: string) => {
    return this._page.getByRole("button", { name: nomDuDomaine });
  };
}

test.describe("Inscription élève - Mes domaines", () => {
  test("L'élève doit choisir au moins un domaine", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.toast()).toContainText(i18n.ÉLÈVE.DOMAINES.SÉLECTIONNE_AU_MOINS_UN);
  });

  test("Je peux sélectionner des domaines", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_AUDIOVISUEL);

    // THEN
    expect(await testhelper.boutonDomaine(testhelper.DOMAINE_AUDIOVISUEL).getAttribute(testhelper.BOUTON_APPUYÉ)).toBe(
      "true",
    );
  });

  test("Je peux supprimer des domaines sélectionnés", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_AUDIOVISUEL);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_AUDIOVISUEL);

    // THEN
    expect(await testhelper.boutonDomaine(testhelper.DOMAINE_AUDIOVISUEL).getAttribute(testhelper.BOUTON_APPUYÉ)).toBe(
      "false",
    );
  });

  test("Si au moins un domaine est renseigné, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
    await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_AUDIOVISUEL);
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
      await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_ARTS_CULTURE);
      await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_AUDIOVISUEL);
      await testhelper.cliquerSurUneCatégorie(testhelper.CATÉGORIE_COMMERCE);
      await testhelper.cliquerSurUnDomaine(testhelper.DOMAINE_COMMERCE_VENTE);
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.boutonDomaine(testhelper.DOMAINE_AUDIOVISUEL)).toBeVisible();
      await expect(testhelper.boutonDomaine(testhelper.DOMAINE_COMMERCE_VENTE)).toBeVisible();
      expect(
        await testhelper.boutonDomaine(testhelper.DOMAINE_AUDIOVISUEL).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
      expect(
        await testhelper.boutonDomaine(testhelper.DOMAINE_COMMERCE_VENTE).getAttribute(testhelper.BOUTON_APPUYÉ),
      ).toBe("true");
    });
  });
});
