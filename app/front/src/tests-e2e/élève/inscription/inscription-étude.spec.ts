import { i18n } from "@/configuration/i18n/i18n";
import { InscriptionTestHelper } from "@/tests-e2e/élève/helpers/InscriptionTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public COMMUNE_RECHERCHÉE = "Str";

  public PREMIÈRE_COMMUNE = "Strasbourg";

  public SECONDE_COMMUNE = "Strazeele";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/etude", "/eleve/inscription/formations", {
      situation: "quelques_pistes",
      classe: "premiere",
      bac: "Générale",
      spécialités: [],
      domaines: ["T_ITM_1534"],
      centresIntérêts: ["travail_manuel_bricoler"],
      métiersFavoris: [],
    });
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

  public renseignerChampRechercheCommunes = async (recherche: string) => {
    await this.renseignerChampRechercheSélecteurMultiple(i18n.ÉLÈVE.ÉTUDE.COMMUNES_ENVISAGÉES.LABEL, recherche);
  };
}

test.describe("Inscription élève - Mes études", () => {
  test("Aucun champ n'est obligatoire pour passer à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test("Je peux obtenir des suggestions de communes en fonction de ma recherche", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheCommunes(testhelper.COMMUNE_RECHERCHÉE);

    // THEN
    await expect(testhelper.listeDesFavorisSuggérés().getByRole("listitem")).toHaveCount(5);
  });

  test("Je peux sélectionner des communes", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheCommunes(testhelper.COMMUNE_RECHERCHÉE);
    await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_COMMUNE).click();
    await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_COMMUNE).click();

    // THEN
    await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
  });

  test("Je peux supprimer des communes sélectionnées", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheCommunes(testhelper.COMMUNE_RECHERCHÉE);
    await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_COMMUNE).click();
    await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_COMMUNE).click();
    await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_COMMUNE).click();

    // THEN
    await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(1);
  });

  test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampRechercheCommunes("blablablabla");

    // THEN
    await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampDuréeÉtudesPrévue(i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.courte.LABEL);
      await testhelper.renseignerChampAlternance(i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.interesse.LABEL);
      await testhelper.renseignerChampRechercheCommunes(testhelper.COMMUNE_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_COMMUNE).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_COMMUNE).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champDuréeÉtudesPrévue()).toHaveValue("courte");
      await expect(testhelper.champAlternance()).toHaveValue("interesse");
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonFavoriSélectionné(testhelper.PREMIÈRE_COMMUNE)).toBeVisible();
      await expect(testhelper.boutonFavoriSélectionné(testhelper.SECONDE_COMMUNE)).toBeVisible();
    });
  });
});
