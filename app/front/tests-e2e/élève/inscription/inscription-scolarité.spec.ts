import { i18n } from "../../../src/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class TestHelper {
  public SÉRIE_GÉNÉRALE = "Série Générale";

  public ARTS_PLASTIQUES = "Arts Plastiques";

  public HISTOIRE_DES_ARTS = "Histoire des arts";

  public constructor(private _page: Page) {}

  public readonly urlPageSuivante = "/eleve/inscription/domaines";

  public naviguerVersLaPage = async () => {
    await this._page.goto("/eleve/inscription/scolarite");
  };

  public soumettreLeFormulaire = async () => {
    await this._boutonSoumissionFormulaire().click();
  };

  public revenirÀLÉtapePrécédente = async () => {
    await this._boutonRetour().click();
  };

  public renseignerCorrectementLeFormulaire = async ({ classeActuelle }: { classeActuelle: string }) => {
    await this.renseignerChampClasseActuelle(classeActuelle);
  };

  public renseignerChampClasseActuelle = async (optionLabel: string) => {
    await this.champClasseActuelle().selectOption({ label: optionLabel });
  };

  public renseignerChampBac = async (optionLabel: string) => {
    await this.champBac().selectOption({ label: optionLabel });
  };

  public renseignerChampSpécialités = async (recherche: string) => {
    await this.champSpécialités().fill(recherche);
  };

  public champClasseActuelle = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LABEL);
  };

  public listeDesSuggestionsSpécialités = () => {
    return this._page.getByTestId("suggérées");
  };

  public listeDesSpécialitésSélectionnées = () => {
    return this._page.getByTestId("sélectionnées");
  };

  public boutonSuggestionSpécialité = (nomSpécialité: string) => {
    return this.listeDesSuggestionsSpécialités().getByRole("button", { name: nomSpécialité });
  };

  public boutonSpécialitéSélectionnée = (nomSpécialité: string) => {
    return this.listeDesSpécialitésSélectionnées().getByRole("button", { name: nomSpécialité });
  };

  public champBac = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.SCOLARITÉ.BAC.LABEL);
  };

  public champSpécialités = () => {
    return this._page.getByRole("searchbox", { name: i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL });
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

test.describe("Inscription élève - Ma scolarité", () => {
  test("Le champ classe actuelle est obligatoire", async ({ page }) => {
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
      classeActuelle: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.PREMIÈRE.LABEL,
    });
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("Si je sélectionne un bac qui a des enseignements de spécialités", () => {
    test("Afficher le champ enseignements de spécialités", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);

      // THEN
      await expect(testhelper.champSpécialités()).toBeVisible();
    });

    test("Je peux obtenir des suggestions de spécialités en fonction de ma recherche", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);
      await testhelper.renseignerChampSpécialités("arts");

      // THEN
      await expect(testhelper.listeDesSuggestionsSpécialités().getByRole("listitem")).toHaveCount(3);
    });

    test("Je peux sélectionner des spécialités", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);
      await testhelper.renseignerChampSpécialités("arts");
      await testhelper.boutonSuggestionSpécialité(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonSuggestionSpécialité(testhelper.HISTOIRE_DES_ARTS).click();

      // THEN
      await expect(testhelper.listeDesSuggestionsSpécialités().getByRole("listitem")).toHaveCount(1);
      await expect(testhelper.listeDesSpécialitésSélectionnées().getByRole("listitem")).toHaveCount(2);
    });

    test("Je peux supprimer des spécialités sélectionnées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);
      await testhelper.renseignerChampSpécialités("arts");
      await testhelper.boutonSuggestionSpécialité(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonSuggestionSpécialité(testhelper.HISTOIRE_DES_ARTS).click();
      await testhelper.boutonSpécialitéSélectionnée(testhelper.ARTS_PLASTIQUES).click();

      // THEN
      await expect(testhelper.listeDesSuggestionsSpécialités().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.listeDesSpécialitésSélectionnées().getByRole("listitem")).toHaveCount(1);
    });

    test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);
      await testhelper.renseignerChampSpécialités("blablablabla");

      // THEN
      await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
    });
  });

  test.describe("Si j'avais sélectionné des spécialités", () => {
    test("En changeant de bac elles sont réinitialisées", async ({ page }) => {
      // GIVEN
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);
      await testhelper.renseignerChampSpécialités("arts");
      await testhelper.boutonSuggestionSpécialité(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonSuggestionSpécialité(testhelper.HISTOIRE_DES_ARTS).click();
      await testhelper.renseignerChampBac("Bac Pro Agricole");

      // THEN
      await expect(testhelper.champSpécialités()).toHaveText("");
      await expect(testhelper.listeDesSuggestionsSpécialités().getByRole("listitem")).toHaveCount(0);
      await expect(testhelper.listeDesSpécialitésSélectionnées().getByRole("listitem")).toHaveCount(0);
    });
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const classeActuelleÀSélectionner = i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.PREMIÈRE.LABEL;
      const testhelper = new TestHelper(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        classeActuelle: classeActuelleÀSélectionner,
      });
      await testhelper.renseignerChampBac(testhelper.SÉRIE_GÉNÉRALE);
      await testhelper.renseignerChampSpécialités("arts");
      await testhelper.boutonSuggestionSpécialité(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonSuggestionSpécialité(testhelper.HISTOIRE_DES_ARTS).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champClasseActuelle()).toHaveValue("premiere");
      await expect(testhelper.champBac()).toHaveValue("Générale");
      await expect(testhelper.listeDesSpécialitésSélectionnées().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonSpécialitéSélectionnée(testhelper.ARTS_PLASTIQUES)).toBeVisible();
      await expect(testhelper.boutonSpécialitéSélectionnée(testhelper.HISTOIRE_DES_ARTS)).toBeVisible();
    });
  });
});
