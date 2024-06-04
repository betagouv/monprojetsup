import { i18n } from "../../../src/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class TestHelper {
  public constructor(private _page: Page) {}

  public readonly urlPageSuivante = "/eleve/inscription/scolarite";

  public naviguerVersLaPage = async () => {
    await this._page.goto("/eleve/inscription/projet");
  };

  public soumettreLeFormulaire = async () => {
    await this._boutonSoumissionFormulaire().click();
  };

  public revenirÀLÉtapePrécédente = async () => {
    await this._boutonRetour().click();
  };

  public renseignerCorrectementLeFormulaire = async ({ situation: string }) => {
    await this.renseignerChampSituation(string);
  };

  public renseignerChampSituation = async (optionLabel: string) => {
    await this.champSituation(optionLabel).check({ force: true });
  };

  public champSituation = (optionLabel: string) => {
    return this._page.getByRole("radio", { name: optionLabel });
  };

  public messageErreurChampObligatoire = () => {
    return this._page.getByText(i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE);
  };

  private _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.CONTINUER });
  };

  private _boutonRetour = () => {
    return this._page.getByRole("link", { name: i18n.COMMUN.RETOUR });
  };
}

test.describe("Inscription élève - Mon projet", () => {
  test("Le champ situation est obligatoire", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurChampObligatoire()).toBeVisible();
  });

  test("Si tous les champs sont renseignés, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerCorrectementLeFormulaire({
      situation: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
    });
    await testhelper.soumettreLeFormulaire();

    // // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const situationÀSélectionner = i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL;
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: situationÀSélectionner,
      });
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champSituation(situationÀSélectionner)).toBeChecked();
    });
  });
});
