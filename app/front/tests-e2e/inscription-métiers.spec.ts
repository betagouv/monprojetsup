import { i18n } from "../src/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class TestHelper {
  public MÉTIER_GESTIONNAIRE_SPORT = "Gestionnaire du sport";

  public MÉTIER_GESTIONNAIRE_ASSURANCE = "Gestionnaire de contrats d'assurance";

  public constructor(private _page: Page) {}

  public readonly urlPageSuivante = "/inscription/etude";

  public naviguerVersLaPage = async () => {
    await this._page.goto("/inscription/metiers");
  };

  public soumettreLeFormulaire = async () => {
    await this._boutonSoumissionFormulaire().click();
  };

  public revenirÀLÉtapePrécédente = async () => {
    await this._boutonRetour().click();
  };

  public renseignerCorrectementLeFormulaire = async ({ situation }: { situation: string }) => {
    await this.renseignerChampSituationMétier(situation);
  };

  public renseignerChampSituationMétier = async (optionLabel: string) => {
    await this.champSituation(optionLabel).check({ force: true });
  };

  public champSituation = (optionLabel: string) => {
    return this._page.getByRole("radio", { name: optionLabel });
  };

  public renseignerChampMétiersEnvisagés = async (recherche: string) => {
    await this.champMétiersEnvisagés().fill(recherche);
  };

  public champMétiersEnvisagés = () => {
    return this._page.getByRole("searchbox", { name: i18n.ÉLÈVE.MÉTIERS.MÉTIERS_ENVISAGÉS.LABEL });
  };

  public listeDesSuggestionsMétiers = () => {
    return this._page.getByTestId("suggérées");
  };

  public listeDesMétiersSélectionnés = () => {
    return this._page.getByTestId("sélectionnées");
  };

  public boutonSuggestionMétier = (nomMétier: string) => {
    return this.listeDesSuggestionsMétiers().getByRole("button", { name: nomMétier });
  };

  public boutonMétierSélectionné = (nomMétier: string) => {
    return this.listeDesMétiersSélectionnés().getByRole("button", { name: nomMétier });
  };

  public messageErreurChampObligatoire = () => {
    return this._page.getByText(i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE);
  };

  public messageErreurAucunRésultat = () => {
    return this._page.getByText(i18n.COMMUN.ERREURS_FORMULAIRES.AUCUN_RÉSULTAT);
  };

  private _boutonSoumissionFormulaire = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.CONTINUER });
  };

  private _boutonRetour = () => {
    return this._page.getByRole("link", { name: i18n.COMMUN.RETOUR });
  };
}

test.describe("Inscription élève - Métiers", () => {
  test("Le champ avancement est obligatoire", async ({ page }) => {
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
    await testhelper.renseignerCorrectementLeFormulaire({
      situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
    });
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("Si j'indique que j'ai quelques idées de métiers", () => {
    test("Afficher le champ métiers envisagés", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });

      // THEN
      await expect(testhelper.champMétiersEnvisagés()).toBeVisible();
    });

    test("Je peux obtenir des suggestions de métiers en fonction de ma recherche", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });
      await testhelper.renseignerChampMétiersEnvisagés("gestionnaire");

      // THEN
      await expect(testhelper.listeDesSuggestionsMétiers().getByRole("listitem")).toHaveCount(8);
    });

    test("Je peux sélectionner des métiers", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });
      await testhelper.renseignerChampMétiersEnvisagés("gestionnaire");
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_SPORT).click();
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_ASSURANCE).click();

      // THEN
      await expect(testhelper.listeDesSuggestionsMétiers().getByRole("listitem")).toHaveCount(6);
      await expect(testhelper.listeDesMétiersSélectionnés().getByRole("listitem")).toHaveCount(2);
    });

    test("Je peux supprimer des métiers sélectionnés", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });
      await testhelper.renseignerChampMétiersEnvisagés("gestionnaire");
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_SPORT).click();
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_ASSURANCE).click();
      await testhelper.boutonMétierSélectionné(testhelper.MÉTIER_GESTIONNAIRE_SPORT).click();

      // THEN
      await expect(testhelper.listeDesSuggestionsMétiers().getByRole("listitem")).toHaveCount(7);
      await expect(testhelper.listeDesMétiersSélectionnés().getByRole("listitem")).toHaveCount(1);
    });

    test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });
      await testhelper.renseignerChampMétiersEnvisagés("blablablabla");

      // THEN
      await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
    });
  });

  test.describe("Si j'avais sélectionné des métiers", () => {
    test("En changeant d'option pour indiquer que je n'ai plus d'idées de métiers ils sont réinitialisés", async ({
      page,
    }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });
      await testhelper.renseignerChampMétiersEnvisagés("gestionnaire");
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_SPORT).click();
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
      });
      await testhelper.renseignerCorrectementLeFormulaire({
        situation: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      });

      // THEN
      await expect(testhelper.champMétiersEnvisagés()).toHaveText("");
      await expect(testhelper.listeDesSuggestionsMétiers().getByRole("listitem")).toHaveCount(0);
      await expect(testhelper.listeDesMétiersSélectionnés().getByRole("listitem")).toHaveCount(0);
    });
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();
      const situationÀSélectionner = i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL;

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({ situation: situationÀSélectionner });
      await testhelper.renseignerChampMétiersEnvisagés("gestionnaire");
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_SPORT).click();
      await testhelper.boutonSuggestionMétier(testhelper.MÉTIER_GESTIONNAIRE_ASSURANCE).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champSituation(situationÀSélectionner)).toBeChecked();
      await expect(testhelper.listeDesMétiersSélectionnés().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonMétierSélectionné(testhelper.MÉTIER_GESTIONNAIRE_SPORT)).toBeVisible();
      await expect(testhelper.boutonMétierSélectionné(testhelper.MÉTIER_GESTIONNAIRE_ASSURANCE)).toBeVisible();
    });
  });
});
