import { i18n } from "@/configuration/i18n/i18n";
import { InscriptionTestHelper } from "@/tests-e2e/élève/helpers/InscriptionTestHelper";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public SPÉCIALITÉ_RECHERCHÉE = "arts";

  public BAC_GÉNÉRAL = "Bac Général";

  public BAC_STAV = "Bac STAV";

  public ARTS_PLASTIQUES = "Arts Plastiques (AP)";

  public HISTOIRE_DES_ARTS = "Histoire des arts";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/scolarite", "/eleve/inscription/domaines", { situation: "quelques_pistes" });
  }

  public renseignerCorrectementLeFormulaire = async ({ classeActuelle }: { classeActuelle: string }) => {
    await this.renseignerChampClasseActuelle(classeActuelle);
  };

  public renseignerChampClasseActuelle = async (optionLabel: string) => {
    await this.champClasseActuelle().selectOption({ label: optionLabel });
  };

  public renseignerChampBac = async (optionLabel: string) => {
    await this.champBac().selectOption({ label: optionLabel });
  };

  public renseignerChampRechercheSpécialités = async (recherche: string) => {
    await this.champSpécialités().fill(recherche);
  };

  public champClasseActuelle = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.SCOLARITÉ.CLASSE.LABEL);
  };

  public champBac = () => {
    return this._page.getByLabel(i18n.ÉLÈVE.SCOLARITÉ.BAC.LABEL);
  };

  public champSpécialités = () => {
    return this.champRechercheSélecteurMultiple(i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL);
  };
}

test.describe("Inscription élève - Ma scolarité", () => {
  test("Le champ classe actuelle est obligatoire", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurChampObligatoire()).toBeVisible();
  });

  test("Si tous les champs obligatoires sont renseignés, passage à l'étape suivante", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.renseignerCorrectementLeFormulaire({
      classeActuelle: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.premiere.LABEL,
    });
    await testhelper.soumettreLeFormulaire();

    // THEN
    expect(page.url()).toContain(testhelper.urlPageSuivante);
  });

  test.describe("Si je sélectionne un bac qui a des enseignements de spécialités", () => {
    test("Afficher le champ enseignements de spécialités", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);

      // THEN
      await expect(testhelper.champRechercheSélecteurMultiple(i18n.ÉLÈVE.SCOLARITÉ.SPÉCIALITÉS.LABEL)).toBeVisible();
    });

    test("Je peux obtenir des suggestions de spécialités en fonction de ma recherche", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);
      await testhelper.renseignerChampRechercheSpécialités(testhelper.SPÉCIALITÉ_RECHERCHÉE);

      // THEN
      await expect(testhelper.listeDesFavorisSuggérés().getByRole("listitem")).toHaveCount(3);
    });

    test("Je peux sélectionner des spécialités", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);
      await testhelper.renseignerChampRechercheSpécialités(testhelper.SPÉCIALITÉ_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonFavoriSuggéré(testhelper.HISTOIRE_DES_ARTS).click();

      // THEN
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
    });

    test("Je peux supprimer des spécialités sélectionnées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);
      await testhelper.renseignerChampRechercheSpécialités(testhelper.SPÉCIALITÉ_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonFavoriSuggéré(testhelper.HISTOIRE_DES_ARTS).click();
      await testhelper.boutonFavoriSuggéré(testhelper.ARTS_PLASTIQUES).click();

      // THEN
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(1);
    });

    test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);
      await testhelper.renseignerChampRechercheSpécialités("blablablabla");

      // THEN
      await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
    });
  });

  test.describe("Si j'avais sélectionné des spécialités", () => {
    test("En changeant de bac elles sont réinitialisées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);
      await testhelper.renseignerChampRechercheSpécialités(testhelper.SPÉCIALITÉ_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonFavoriSuggéré(testhelper.HISTOIRE_DES_ARTS).click();
      await testhelper.renseignerChampBac(testhelper.BAC_STAV);

      // THEN
      await expect(testhelper.champSpécialités()).toHaveText("");
      await expect(testhelper.listeDesFavorisSuggérés().getByRole("listitem")).toHaveCount(0);
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(0);
    });
  });

  test.describe("En étant à l'étape suivante", () => {
    test("Au clic sur le bouton retour je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const classeActuelleÀSélectionner = i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS.premiere.LABEL;
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerCorrectementLeFormulaire({
        classeActuelle: classeActuelleÀSélectionner,
      });
      await testhelper.renseignerChampBac(testhelper.BAC_GÉNÉRAL);
      await testhelper.renseignerChampRechercheSpécialités(testhelper.SPÉCIALITÉ_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.ARTS_PLASTIQUES).click();
      await testhelper.boutonFavoriSuggéré(testhelper.HISTOIRE_DES_ARTS).click();
      await testhelper.soumettreLeFormulaire();
      await testhelper.revenirÀLÉtapePrécédente();

      // THEN
      await expect(testhelper.champClasseActuelle()).toHaveValue("premiere");
      await expect(testhelper.champBac()).toHaveValue("Générale");
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonFavoriSélectionné(testhelper.ARTS_PLASTIQUES)).toBeVisible();
      await expect(testhelper.boutonFavoriSélectionné(testhelper.HISTOIRE_DES_ARTS)).toBeVisible();
    });
  });
});
