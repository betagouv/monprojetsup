import { i18n } from "@/configuration/i18n/i18n";
import { TestHelper } from "@/tests-e2e/testHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends TestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public naviguerVersLaPage = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto("/profil");
  };

  public soumettreLeFormulaire = async () => {
    await this._boutonSoumissionFormulaire().click();
  };

  public cliquerSurUnOnglet = async (nom: string) => {
    await this.unOnglet(nom).click();
  };

  public titreDeOngletActif = () => {
    return this._page.locator(".fr-tabs__panel--selected").getByRole("heading", { level: 2 });
  };

  public renseignerChampClasseActuelle = async (optionLabel: string) => {
    await this.champClasseActuelle().selectOption({ label: optionLabel });
  };

  public champClasseActuelle = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LABEL);
  };

  public unOnglet = (nom: string) => {
    return this.listeOnglets().getByRole("tab", { name: nom });
  };

  public listeOnglets = () => {
    return this._page.getByRole("tablist", { name: i18n.PAGE_PROFIL.TITRE });
  };

  private _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.ENREGISTRER });
  };
}

test.describe("Page Profil Élève", () => {
  test("J'ai accès à 4 onglets me permettant de modifier mon profil", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // THEN
    await expect(testhelper.listeOnglets().getByRole("tab")).toHaveCount(5);
  });

  test("Je peux naviguer entre les onglets", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.cliquerSurUnOnglet(i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE_ÉTAPE);

    // THEN
    await expect(testhelper.titreDeOngletActif()).toHaveText(i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE);
  });

  test("Je peux modifier un élément", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerChampClasseActuelle(i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.terminale.LABEL);
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.toast()).toContainText(i18n.COMMUN.MODIFICATIONS_ENREGISTRÉES);
  });
});
