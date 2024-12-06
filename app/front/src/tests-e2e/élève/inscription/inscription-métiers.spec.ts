/* eslint-disable sonarjs/no-nested-functions */
import { InscriptionTestHelper } from "./inscriptionTestHelper";
import { i18n } from "@/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public MÉTIER_RECHERCHÉ = "gestionnaire";

  public PREMIER_MÉTIER = "gestionnaire du sport";

  public SECOND_MÉTIER = "gestionnaire de contrats d'assurance";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/metiers", "/eleve/inscription/etude", {
      situation: "quelques_pistes",
      classe: "premiere",
      bac: "Générale",
      spécialités: [],
      domaines: ["T_ITM_1534"],
      centresIntérêts: ["travail_manuel_bricoler"],
    });
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

  public messageErreurAucunMétierFavoriSélectionné = () => {
    return this._page.getByText(
      `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_UN} ${i18n.COMMUN.MÉTIER.toLocaleLowerCase()}`,
    );
  };
}

test.describe("Inscription élève - Métiers", () => {
  test("Le champ situation est obligatoire", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurChampObligatoire()).toBeVisible();
  });

  test.describe("Si j'indique que j'ai quelques idées de métiers", () => {
    test("Je peux obtenir des suggestions de métiers en fonction de ma recherche", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);

      // THEN
      await expect(testhelper.listeDesFavorisSuggérés().getByRole("listitem")).toHaveCount(8);
    });

    test("Je peux sélectionner des métiers", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECOND_MÉTIER).click();

      // THEN
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
    });

    test("Je peux supprimer des métiers sélectionnés", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECOND_MÉTIER).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECOND_MÉTIER).click();

      // THEN
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(1);
    });

    test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
      await testhelper.renseignerChampRechercheMétiers("blablablabla");

      // THEN
      await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
    });

    test.describe("Si je ne mets aucun métier en favori", () => {
      test("Je ne peux pas soumettre le formulaire et j'ai un message d'erreur qui s'affiche", async ({ page }) => {
        // GIVEN
        const testhelper = new Test(page);
        await testhelper.naviguerVersLaPage();

        // WHEN
        await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
        await testhelper.soumettreLeFormulaire();

        // THEN
        await expect(testhelper.messageErreurAucunMétierFavoriSélectionné()).toBeVisible();
      });
    });
  });

  test.describe("Si j'ai sélectionné des métiers", () => {
    test.describe("Si je change le champ 'Avancement' à 'Aucune idée'", () => {
      test.describe("Si je change à nouveau le champ 'Avancement' à 'Quelques pistes'", () => {
        test("mes métiers précédemment sélectionnés sont toujours présents", async ({ page }) => {
          // GIVEN
          const testhelper = new Test(page);
          await testhelper.naviguerVersLaPage();

          // WHEN
          await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
          await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
          await testhelper.boutonFavoriSuggéré(testhelper.PREMIER_MÉTIER).click();
          await testhelper.boutonFavoriSuggéré(testhelper.SECOND_MÉTIER).click();
          await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL);
          await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);

          // THEN
          await expect(
            testhelper.champRechercheSélecteurMultiple(i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL),
          ).toHaveText("");
          await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
        });
      });

      test.describe("Si je soumets je formulaire", () => {
        test("mes métiers précédemment sélectionnés sont supprimés", async ({ page }) => {
          // GIVEN
          const testhelper = new Test(page);
          await testhelper.naviguerVersLaPage();

          // WHEN
          await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);
          await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
          await testhelper.boutonFavoriSuggéré(testhelper.PREMIER_MÉTIER).click();
          await testhelper.boutonFavoriSuggéré(testhelper.SECOND_MÉTIER).click();
          await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL);
          await testhelper.soumettreLeFormulaire();
          await testhelper.revenirÀLÉtapePrécédente();
          await testhelper.renseignerChampSituationMétiers(i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL);

          // THEN
          await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(0);
        });
      });
    });
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();
      const situationMétiers = i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL;

      // WHEN
      await testhelper.renseignerChampSituationMétiers(situationMétiers);
      await testhelper.renseignerChampRechercheMétiers(testhelper.MÉTIER_RECHERCHÉ);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIER_MÉTIER).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECOND_MÉTIER).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champSituationMétiers(situationMétiers)).toBeChecked();
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonFavoriSélectionné(testhelper.PREMIER_MÉTIER)).toBeVisible();
      await expect(testhelper.boutonFavoriSélectionné(testhelper.SECOND_MÉTIER)).toBeVisible();
    });
  });
});
