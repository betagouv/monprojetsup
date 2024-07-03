import { i18n } from "../../../src/configuration/i18n/i18n";
import { InscriptionTestHelper } from "./InscriptionTestHelper";
import { expect, type Page, test } from "@playwright/test";

class TestHelper extends InscriptionTestHelper {
  public VILLE_RECHERCHÉE = "Brest";

  public PREMIÈRE_VILLE = "Brest";

  public SECONDE_VILLE = "Brestot";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/etude", "/eleve/inscription/formations");
  }

  public champDuréeÉtudesPrévue = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.LABEL);
  };

  public champAlternance = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.LABEL);
  };

  public renseignerChampDuréeÉtudesPrévue = async (optionLabel: string) => {
    await this.champDuréeÉtudesPrévue().selectOption({ label: optionLabel });
  };

  public renseignerChampAlternance = async (optionLabel: string) => {
    await this.champAlternance().selectOption({ label: optionLabel });
  };

  public renseignerChampRechercheVilles = async (recherche: string) => {
    await this.renseignerChampRechercheSélecteurMultiple(i18n.ÉLÈVE.ÉTUDE.VILLES_ENVISAGÉES.LABEL, recherche);
  };
}

test.describe("Inscription élève - Mes études", () => {
  test("Aucun champ n'est obligatoire pour passer à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test("Je peux obtenir des suggestions de villes en fonction de ma recherche", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheVilles(testhelper.VILLE_RECHERCHÉE);

    // THEN
    await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(3);
  });

  test("Je peux sélectionner des villes", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheVilles(testhelper.VILLE_RECHERCHÉE);
    await testhelper.boutonOptionSuggérée(testhelper.PREMIÈRE_VILLE).click();
    await testhelper.boutonOptionSuggérée(testhelper.SECONDE_VILLE).click();

    // THEN
    await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(1);
    await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(2);
  });

  test("Je peux supprimer des villes sélectionnées", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheVilles(testhelper.VILLE_RECHERCHÉE);
    await testhelper.boutonOptionSuggérée(testhelper.PREMIÈRE_VILLE).click();
    await testhelper.boutonOptionSuggérée(testhelper.SECONDE_VILLE).click();
    await testhelper.boutonOptionSélectionnée(testhelper.SECONDE_VILLE).click();

    // THEN
    await expect(testhelper.listeDesOptionsSuggérées().getByRole("listitem")).toHaveCount(2);
    await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(1);
  });

  test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
    // GIVEN
    const testhelper = new TestHelper(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheVilles("blablablabla");

    // THEN
    await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampDuréeÉtudesPrévue(i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.COURTE.LABEL);
      await testhelper.renseignerChampAlternance(i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.INTÉRESSÉ.LABEL);
      await testhelper.renseignerChampRechercheVilles(testhelper.VILLE_RECHERCHÉE);
      await testhelper.boutonOptionSuggérée(testhelper.PREMIÈRE_VILLE).click();
      await testhelper.boutonOptionSuggérée(testhelper.SECONDE_VILLE).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champDuréeÉtudesPrévue()).toHaveValue("courte");
      await expect(testhelper.champAlternance()).toHaveValue("interesse");
      await expect(testhelper.listeDesOptionsSélectionnées().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonOptionSélectionnée(testhelper.PREMIÈRE_VILLE)).toBeVisible();
      await expect(testhelper.boutonOptionSélectionnée(testhelper.SECONDE_VILLE)).toBeVisible();
    });
  });
});
