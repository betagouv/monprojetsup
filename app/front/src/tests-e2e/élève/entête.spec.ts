import { ÉlèveTestHelper } from "./helpers/ÉlèveTestHelper";
import { i18n } from "@/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class Test extends ÉlèveTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public naviguerVersLaPageDeDébutDeParcoursInscription = async () => {
    await this.initialiserProfilÉlèveParDéfaut({});
    await this._page.goto("/eleve/inscription/scolarite");
  };

  public naviguerVersLaPageAvecParcoursInscriptionTerminé = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto("/");
  };

  public boutonSeDéconnecterParcoursInscription = () => {
    return this._page.getByRole("button", { name: i18n.PAGE_PROFIL.SE_DÉCONNECTER });
  };
}

test.describe("Entête", () => {
  test.describe("Si je suis connecté et que je suis dans le parcours d'inscription", () => {
    test("Je peux me deconnecter", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageDeDébutDeParcoursInscription();

      // THEN
      await expect(testhelper.boutonSeDéconnecterParcoursInscription()).toBeVisible();
    });
  });

  test.describe("Si je suis connecté et que j'ai suivi le parcours d'inscription", () => {
    test("J'ai accès à 3 liens me permettant de naviguer vers le tableau de bord, les formations et mes favoris", async ({
      page,
    }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageAvecParcoursInscriptionTerminé();

      // THEN
      await expect(testhelper.lien(i18n.NAVIGATION.TABLEAU_DE_BORD)).toBeVisible();
      await expect(testhelper.lien(i18n.NAVIGATION.FORMATIONS)).toBeVisible();
      await expect(testhelper.lien(i18n.NAVIGATION.FAVORIS)).toBeVisible();
      await expect(testhelper.lien(i18n.NAVIGATION.TABLEAU_DE_BORD)).toHaveAttribute(
        "href",
        testhelper.PAGE_TABLEAU_DE_BORD,
      );
      await expect(testhelper.lien(i18n.NAVIGATION.FORMATIONS)).toHaveAttribute("href", testhelper.PAGE_FORMATIONS);
      await expect(testhelper.lien(i18n.NAVIGATION.FAVORIS)).toHaveAttribute("href", testhelper.PAGE_FAVORIS);
    });

    test("Je peux cliquer sur mon nom pour accéder à mon profil", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageAvecParcoursInscriptionTerminé();

      // THEN
      await expect(testhelper.lien(testhelper.NOM_UTILISATEUR)).toBeVisible();
      await expect(testhelper.lien(testhelper.NOM_UTILISATEUR)).toHaveAttribute("href", testhelper.PAGE_PROFIL);
    });

    test("Je peux me rendre sur la PFA", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);

      // WHEN
      await testhelper.naviguerVersLaPageAvecParcoursInscriptionTerminé();

      // THEN
      await expect(testhelper.lien(i18n.ENTÊTE.PLATEFORME_AVENIRS)).toBeVisible();
      await expect(testhelper.lien(i18n.ENTÊTE.PLATEFORME_AVENIRS)).toHaveAttribute(
        "href",
        process.env.VITE_AVENIRS_URL as string,
      );
    });
  });
});
