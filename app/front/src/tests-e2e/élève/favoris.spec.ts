import { ListeEtAperçuTestHelper } from "./helpers/ListeEtAperçuTestHelper";
import { i18n } from "@/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class Test extends ListeEtAperçuTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public lienExplorerFormations = () => {
    return this._page.getByRole("main").getByRole("link", { name: i18n.PAGE_FAVORIS.AUCUN_FAVORI.BOUTON });
  };

  public cliquerSurCatégorieFormation = async () => {
    return this._sélecteurCatégorie().getByText(i18n.COMMUN.FORMATION).click();
  };

  public cliquerSurCatégorieMétier = async () => {
    return this._sélecteurCatégorie().getByText(i18n.COMMUN.MÉTIER).click();
  };

  public _sélecteurCatégorie = () => {
    return this._page.getByRole("main").getByRole("group", { name: i18n.ACCESSIBILITÉ.CATÉGORIE });
  };
}

test.describe("Page Favoris Élève", () => {
  test.describe("Formations", () => {
    test("Par défaut je peux voir mes formations favorites", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();

      // THEN
      await expect(testhelper.cartesFormations()).toHaveCount(testhelper.NOMBRE_FORMATIONS_FAVORITES);
    });

    test("Par défaut je vois la fiche détaillée de ma première formation favorite", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      const formations = await testhelper.récupérerToutesFormations();

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();

      // THEN
      expect(await testhelper.contenuDeLaPremièreCarteFormation()).toContain(formations[0]?.nom);
      expect(await testhelper.titrePage()).toBe(formations[0]?.nom);
      expect(page.url()).toContain(`${testhelper.PAGE_FAVORIS}#${formations[0]?.id}`);
    });

    test("Je peux changer de fiche formation en cliquant sur une carte", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      const formations = await testhelper.récupérerToutesFormations();

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.cliquerSurLaDernièreCarteFormation();

      // THEN
      const dernièreFormationFavorite = formations[testhelper.NOMBRE_FORMATIONS_FAVORITES - 1];
      expect(await testhelper.contenuDeLaDernièreCarteFormation()).toContain(dernièreFormationFavorite?.nom);
      expect(await testhelper.titrePage()).toBe(dernièreFormationFavorite?.nom);
      expect(page.url()).toContain(`${testhelper.PAGE_FAVORIS}#${dernièreFormationFavorite?.id}`);
    });

    test("Je peux supprimer une formation de mes favoris", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.appuyerSurLeBoutonSupprimerFavori();

      // THEN
      await expect(testhelper.boutonAjouterEnFavori()).toBeVisible();
      expect(await testhelper.contenuDeLaPremièreCarteFormation()).not.toContain(i18n.ACCESSIBILITÉ.FAVORI);
    });

    test("Je peux retourner consulter mes formations favorites après avoir consultés mes métiers", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      const formations = await testhelper.récupérerToutesFormations();

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.cliquerSurCatégorieMétier();
      await testhelper.cliquerSurCatégorieFormation();

      // THEN
      await expect(testhelper.cartesFormations()).toHaveCount(testhelper.NOMBRE_FORMATIONS_FAVORITES);
      expect(await testhelper.contenuDeLaPremièreCarteFormation()).toContain(formations[0]?.nom);
      expect(await testhelper.titrePage()).toBe(formations[0]?.nom);
      expect(page.url()).toContain(`${testhelper.PAGE_FAVORIS}#${formations[0]?.id}`);
    });

    test.describe("Si je n'ai aucune formation favorite", () => {
      test("Je vois un message me l'indiquant et un lien pour accéder à la page formations", async ({ page }) => {
        // GIVEN
        const testhelper = new Test(page);

        // WHEN
        await testhelper.naviguerVersLaPageFavoris();

        // THEN
        expect(await testhelper.titrePage()).toBe(i18n.PAGE_FAVORIS.AUCUN_FAVORI.TEXTE_FORMATIONS);
        await expect(testhelper.lienExplorerFormations()).toHaveAttribute("href", testhelper.PAGE_FORMATIONS);
      });
    });
  });

  test.describe("Métiers", () => {
    test("Je peux voir mes métiers favoris", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.cliquerSurCatégorieMétier();

      // THEN
      await expect(testhelper.cartesMétiers()).toHaveCount(testhelper.NOMBRE_MÉTIERS_FAVORIS);
    });

    test("Par défaut je vois la fiche détaillée de mon premier métier favori", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      const métiers = await testhelper.récupérerTousMétiers();

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.cliquerSurCatégorieMétier();

      // THEN
      expect(await testhelper.contenuDeLaPremièreCarteMétier()).toContain(métiers[0]?.nom);
      expect(await testhelper.titrePage()).toBe(métiers[0]?.nom);
      expect(page.url()).toContain(`${testhelper.PAGE_FAVORIS}#${métiers[0]?.id}`);
    });

    test("Je peux changer de fiche métier en cliquant sur une carte", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      const métiers = await testhelper.récupérerTousMétiers();

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.cliquerSurCatégorieMétier();
      await testhelper.cliquerSurLaDernièreCarteMétier();

      // THEN
      const dernierMétierFavori = métiers[testhelper.NOMBRE_MÉTIERS_FAVORIS - 1];
      expect(await testhelper.contenuDeLaDernièreCarteMétier()).toContain(dernierMétierFavori?.nom);
      expect(await testhelper.titrePage()).toBe(dernierMétierFavori?.nom);
      expect(page.url()).toContain(`${testhelper.PAGE_FAVORIS}#${dernierMétierFavori?.id}`);
    });

    test("Je peux supprimer un métier de mes favoris", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageFavorisAvecDesFavoris();
      await testhelper.cliquerSurCatégorieMétier();
      await testhelper.appuyerSurLeBoutonSupprimerFavori();

      // THEN
      await expect(testhelper.boutonAjouterEnFavori()).toBeVisible();
      expect(await testhelper.contenuDeLaPremièreCarteMétier()).not.toContain(i18n.ACCESSIBILITÉ.FAVORI);
    });

    test.describe("Si je n'ai aucun métier favori", () => {
      test("Je vois un message me l'indiquant et un lien pour accéder à la page formations", async ({ page }) => {
        // GIVEN
        const testhelper = new Test(page);

        // WHEN
        await testhelper.naviguerVersLaPageFavoris();
        await testhelper.cliquerSurCatégorieMétier();

        // THEN
        expect(await testhelper.titrePage()).toBe(i18n.PAGE_FAVORIS.AUCUN_FAVORI.TEXTE_MÉTIERS);
        await expect(testhelper.lienExplorerFormations()).toHaveAttribute("href", testhelper.PAGE_FORMATIONS);
      });
    });
  });
});
