import { i18n } from "@/configuration/i18n/i18n";
import { formationInMemoryRepository } from "@/features/formation/infrastructure/gateway/formationInMemoryRepository/formationInMemoryRepository";
import { ListeEtAperçuTestHelper } from "@/tests-e2e/élève/helpers/ListeEtAperçuTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ListeEtAperçuTestHelper {
  public readonly RECHERCHE = "BTS";

  public constructor(protected _page: Page) {
    super(_page);
  }

  public static readonly rechercheFormation = async (recherche: string) => {
    const formationInMemory = new formationInMemoryRepository();
    const résultatsDeRecherche = await formationInMemory.rechercherFichesFormations(recherche);

    if (résultatsDeRecherche instanceof Error) return [];

    return résultatsDeRecherche;
  };

  public remplirChampDeRechercheFormation = async (recherche: string) => {
    return this._page
      .getByRole("searchbox", {
        name: i18n.PAGE_FORMATION.CHAMP_RECHERCHE_LABEL,
      })
      .fill(recherche);
  };

  public appuyerSurLeBoutonDeRechercheFormation = async () => {
    return this._boutonRechercherFormation().click();
  };

  public appuyerSurLeBoutonRetourAuxSuggestions = async () => {
    return this._boutonRetourSuggestions().click();
  };

  private _boutonRechercherFormation = () => {
    return this._page.getByRole("button", {
      name: i18n.COMMUN.RECHERCHER,
    });
  };

  private _boutonRetourSuggestions = () => {
    return this._page.getByRole("button", {
      name: i18n.PAGE_FORMATION.RETOUR_AUX_SUGGESTIONS,
    });
  };
}

test.describe("Formations - Rechercher", () => {
  test("Je peux chercher un ou plusieurs mots clés et obtenir les formations correspondantes", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const premierRésultatDeRecherche = (await Test.rechercheFormation(testhelper.RECHERCHE)).at(0);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.remplirChampDeRechercheFormation(testhelper.RECHERCHE);
    await testhelper.appuyerSurLeBoutonDeRechercheFormation();

    // THEN
    await expect(testhelper.cartesFormations()).toHaveCount(4);
    expect(await testhelper.contenuDeLaPremièreCarteFormation()).toContain(premierRésultatDeRecherche?.nom);
    expect(await testhelper.titrePage()).toBe(premierRésultatDeRecherche?.nom);
    expect(page.url()).toContain(`${testhelper.PAGE_FORMATIONS}#${premierRésultatDeRecherche?.id}`);
  });

  test("Je peux naviguer entre les résultats", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const dernierRésultatDeRecherche = (await Test.rechercheFormation(testhelper.RECHERCHE)).at(-1);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.remplirChampDeRechercheFormation(testhelper.RECHERCHE);
    await testhelper.appuyerSurLeBoutonDeRechercheFormation();
    await testhelper.cliquerSurLaDernièreCarteFormation();

    // THEN
    expect(await testhelper.contenuDeLaDernièreCarteFormation()).toContain(dernierRésultatDeRecherche?.nom);
    expect(await testhelper.titrePage()).toBe(dernierRésultatDeRecherche?.nom);
    expect(page.url()).toContain(`${testhelper.PAGE_FORMATIONS}#${dernierRésultatDeRecherche?.id}`);
  });

  test("Je peux retourner aux suggestions", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.remplirChampDeRechercheFormation(testhelper.RECHERCHE);
    await testhelper.appuyerSurLeBoutonDeRechercheFormation();
    await testhelper.appuyerSurLeBoutonRetourAuxSuggestions();

    // THEN
    await expect(testhelper.cartesFormations()).toHaveCount(5);
  });
});
