import { i18n } from "@/configuration/i18n/i18n";
import { InscriptionTestHelper } from "@/tests-e2e/élève/helpers/InscriptionTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/projet", "/eleve/inscription/scolarite");
  }

  public revenirÀLÉtapePrécédente = async () => {
    await this._boutonRetour().click();
  };

  public renseignerCorrectementLeFormulaire = async ({ situation }: { situation: string }) => {
    await this.renseignerChampSituation(situation);
  };

  public renseignerChampSituation = async (optionLabel: string) => {
    await this.champSituation(optionLabel).check({ force: true });
  };

  public champSituation = (optionLabel: string) => {
    return this._page.getByRole("radio", { name: optionLabel });
  };
}

test.describe("Inscription élève - Mon projet", () => {
  test("Le champ situation est obligatoire", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurChampObligatoire()).toBeVisible();
  });

  test("Si tous les champs sont renseignés, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerCorrectementLeFormulaire({
      situation: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.aucune_idee.LABEL,
    });
    await testhelper.soumettreLeFormulaire();

    // // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const situationÀSélectionner = i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.aucune_idee.LABEL;
      const testhelper = new Test(page);
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
