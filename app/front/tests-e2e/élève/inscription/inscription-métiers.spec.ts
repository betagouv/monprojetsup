import { i18n } from "../../../src/configuration/i18n/i18n";
import { InscriptionTestHelper } from "./InscriptionTestHelper";
import { expect, type Page, test } from "@playwright/test";

class TestHelper extends InscriptionTestHelper {
  public MÉTIER_RECHERCHÉ = "gestionnaire";

  public PREMIER_MÉTIER = "gestionnaire du sport";

  public SECOND_MÉTIER = "gestionnaire de contrats d'assurance";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/metiers", "/eleve/inscription/etude");
  }

  public champSituationMétiers = (optionLabel: string) => {
    return this._page.getByRole("radio", { name: optionLabel });
  };

  public renseignerChampSituationMétiers = async (optionLabel: string) => {
    await this.champSituationMétiers(optionLabel).check({ force: true });
  };

  public renseignerChampRechercheMétiers = async (recherche: string) => {
    await this.renseignerChampRechercheSélecteurMultiple(i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL, recherche);
  };
}

test.describe("Inscription élève - Métiers", () => {
  test("Le champ 'Avancement' est obligatoire", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurChampObligatoire()).toBeVisible();
  });

  test("Si tous les champs obligatoires sont renseignés, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL);
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("Si j'indique que j'ai quelques idées de métiers", () => {
    test("Je peux obtenir des suggestions de métiers en fonction de ma recherche", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);

      // THEN
      await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(8);
    });

    test("Je peux sélectionner des métiers", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonOptionSuggérée(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonOptionSuggérée(testhelper.SECOND_MÉTIER).click();

      // THEN
      await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(6);
      await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(2);
    });

    test("Je peux supprimer des métiers sélectionnés", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonOptionSuggérée(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonOptionSuggérée(testhelper.SECOND_MÉTIER).click();
      await testhelper.boutonOptionSélectionnée(testhelper.SECOND_MÉTIER).click();

      // THEN
      await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(7);
      await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(1);
    });

    test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers("blablablabla");

      // THEN
      await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
    });
  });

  test.describe("Si j'avais sélectionné des métiers", () => {
    test("En changeant le champ 'Avancement' ils sont réinitialisés", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonOptionSuggérée(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonOptionSuggérée(testhelper.SECOND_MÉTIER).click();
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL);
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);

      // THEN
      await expect(testhelper.champRechercheSélecteurMultiple(i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL)).toHaveText(
        "",
      );
      await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(0);
      await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(0);
    });
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();
      const situationMétiers = i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL;

      // WHEN
      await testhelper.renseignerChampSituationMétiers(situationMétiers);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonOptionSuggérée(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonOptionSuggérée(testhelper.SECOND_MÉTIER).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champSituationMétiers(situationMétiers)).toBeChecked();
      await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonOptionSélectionnée(testhelper.PREMIER_MÉTIER)).toBeVisible();
      await expect(testhelper.boutonOptionSélectionnée(testhelper.SECOND_MÉTIER)).toBeVisible();
    });
  });
});
