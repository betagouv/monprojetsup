import { i18n } from "@/configuration/i18n/i18n";
import { ListeEtAperçuTestHelper } from "@/tests-e2e/élève/helpers/ListeEtAperçuTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends ListeEtAperçuTestHelper {
  public constructor(protected _page: Page) {
    super(_page);
  }

  public appuyerSurLeBoutonMétier = async (nomMétier: string) => {
    return this.boutonMétier(nomMétier).click();
  };

  public appuyerSurLeBoutonAjouterEnFavoriMétier = async (nomMétier: string) => {
    return this._boutonAjouterEnFavoriMétier(nomMétier).click();
  };

  public appuyerSurLeBoutonFermerModaleMétier = async (nomMétier: string) => {
    return this._boutonFermerModaleMétier(nomMétier).click();
  };

  public modaleMétier = (nom: string) => {
    return this._page.getByRole("dialog", {
      name: nom,
    });
  };

  public boutonSupprimerEnFavoriMétier = (nom: string) => {
    return this.modaleMétier(nom).getByRole("button", { name: i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION });
  };

  private _boutonFermerModaleMétier = (nom: string) => {
    return this.modaleMétier(nom).getByRole("button", { name: i18n.COMMUN.FERMER }).first();
  };

  public boutonMétier = (nom: string) => {
    return this._page.getByRole("button", {
      name: nom,
    });
  };

  private _boutonAjouterEnFavoriMétier = (nom: string) => {
    return this.modaleMétier(nom).getByRole("button", { name: i18n.COMMUN.AJOUTER_À_MA_SÉLECTION });
  };
}

test.describe("Sur une fiche formation", () => {
  test("Je peux cliquer sur un métier pour avoir plus d'information", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const premièreFormationSuggérée = (await Test.récupérerFormationsSuggérées()).at(0);
    const premierMétierProposé = premièreFormationSuggérée?.métiersAccessibles[0].nom ?? "";

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonMétier(premierMétierProposé);

    // THEN
    await expect(testhelper.modaleMétier(premierMétierProposé)).toBeVisible();
  });

  test("Je peux ajouter un métier en favori", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const premièreFormationSuggérée = (await Test.récupérerFormationsSuggérées()).at(0);
    const premierMétierProposé = premièreFormationSuggérée?.métiersAccessibles[0].nom ?? "";

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonMétier(premierMétierProposé);
    await testhelper.appuyerSurLeBoutonAjouterEnFavoriMétier(premierMétierProposé);

    // THEN
    await expect(testhelper.boutonSupprimerEnFavoriMétier(premierMétierProposé)).toBeVisible();
  });

  test("Je peux fermer la modale", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    const premièreFormationSuggérée = (await Test.récupérerFormationsSuggérées()).at(0);
    const premierMétierProposé = premièreFormationSuggérée?.métiersAccessibles[0].nom ?? "";

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonMétier(premierMétierProposé);
    await testhelper.appuyerSurLeBoutonFermerModaleMétier(premierMétierProposé);

    // THEN
    await expect(testhelper.modaleMétier(premierMétierProposé)).toBeVisible();
  });

  test("Si j'ai ajouté ce métier en favori, l'information apparait avant le nom du métier sur la fiche", async ({
    page,
  }) => {
    // GIVEN
    const testhelper = new Test(page);
    const premièreFormationSuggérée = (await Test.récupérerFormationsSuggérées()).at(0);
    const premierMétierProposé = premièreFormationSuggérée?.métiersAccessibles[0].nom ?? "";

    // WHEN
    await testhelper.naviguerVersLaPageFormations();
    await testhelper.appuyerSurLeBoutonMétier(premierMétierProposé);
    await testhelper.appuyerSurLeBoutonAjouterEnFavoriMétier(premierMétierProposé);
    await testhelper.appuyerSurLeBoutonFermerModaleMétier(premierMétierProposé);

    // THEN
    expect(await testhelper.boutonMétier(premierMétierProposé).textContent()).toContain(i18n.ACCESSIBILITÉ.FAVORI);
  });
});
