import { InscriptionTestHelper } from "./inscriptionTestHelper";
import { i18n } from "@/configuration/i18n/i18n";
import { expect, type Page, test } from "@playwright/test";

class Test extends InscriptionTestHelper {
  public FORMATION_RECHERCHÉE = "tourisme";

  public PREMIÈRE_FORMATION = "L1 - Tourisme -  Accès Santé (LAS)";

  public SECONDE_FORMATION = "BTS - Tourisme - en apprentissage";

  public constructor(protected _page: Page) {
    super(_page, "/eleve/inscription/formations", "/eleve/inscription/confirmation", {
      situation: "quelques_pistes",
      classe: "premiere",
      bac: "Générale",
      spécialités: [],
      domaines: ["T_ITM_1534"],
      centresIntérêts: ["travail_manuel_bricoler"],
      métiersFavoris: [],
      alternance: "indifferent",
      communesFavorites: [],
      duréeÉtudesPrévue: "courte",
    });
  }

  public champSituationFormations = (optionLabel: string) => {
    return this._page.getByRole("radio", { name: optionLabel });
  };

  public renseignerChampSituationFormations = async (optionLabel: string) => {
    await this.champSituationFormations(optionLabel).check({ force: true });
  };

  public renseignerChampRechercheFormations = async (recherche: string) => {
    await this.renseignerChampRechercheSélecteurMultiple(i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.LABEL, recherche);
  };

  public messageErreurAucuneFormationFavoriteSélectionnée = () => {
    return this._page.getByText(
      `${i18n.COMMUN.ERREURS_FORMULAIRES.AU_MOINS_UNE} ${i18n.COMMUN.FORMATION.toLocaleLowerCase()}`,
    );
  };
}

test.describe("Inscription élève - Formations", () => {
  test("Le champ situation est obligatoire", async ({ page }) => {
    // GIVEN
    const testhelper = new Test(page);
    await testhelper.naviguerVersLaPage();

    // WHEN
    await testhelper.soumettreLeFormulaire();

    // THEN
    await expect(testhelper.messageErreurChampObligatoire()).toBeVisible();
  });

  test.describe("Si j'indique que j'ai quelques idées de formations", () => {
    test("Je peux obtenir des suggestions de formations en fonction de ma recherche", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationFormations(
        i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      );
      await testhelper.renseignerChampRechercheFormations(testhelper.FORMATION_RECHERCHÉE);

      // THEN
      await expect(testhelper.listeDesFavorisSuggérés().getByRole("listitem")).toHaveCount(4);
    });

    test("Je peux sélectionner des formations", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationFormations(
        i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      );
      await testhelper.renseignerChampRechercheFormations(testhelper.FORMATION_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_FORMATION).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_FORMATION).click();

      // THEN
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
    });

    test("Je peux supprimer des formations sélectionnées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationFormations(
        i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      );
      await testhelper.renseignerChampRechercheFormations(testhelper.FORMATION_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_FORMATION).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_FORMATION).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_FORMATION).click();

      // THEN
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(1);
    });

    test("Si je cherche quelque chose qui n'existe pas j'ai un message d'erreur qui s'affiche", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationFormations(
        i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      );
      await testhelper.renseignerChampRechercheFormations("blablablabla");

      // THEN
      await expect(testhelper.messageErreurAucunRésultat()).toBeVisible();
    });

    test.describe("Si je ne mets aucune formation en favori", () => {
      test("Je ne peux pas soumettre le formulaire et j'ai un message d'erreur qui s'affiche", async ({ page }) => {
        // GIVEN
        const testhelper = new Test(page);
        await testhelper.naviguerVersLaPage();

        // WHEN
        await testhelper.renseignerChampSituationFormations(
          i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
        );
        await testhelper.soumettreLeFormulaire();

        // THEN
        await expect(testhelper.messageErreurAucuneFormationFavoriteSélectionnée()).toBeVisible();
      });
    });
  });

  test.describe("Si j'avais sélectionné des formations", () => {
    test("En changeant le champ 'Avancement' elles ne sont pas réinitialisées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();

      // WHEN
      await testhelper.renseignerChampSituationFormations(
        i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      );
      await testhelper.renseignerChampRechercheFormations(testhelper.FORMATION_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_FORMATION).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_FORMATION).click();
      await testhelper.renseignerChampSituationFormations(i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL);
      await testhelper.renseignerChampSituationFormations(
        i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      );

      // THEN
      await expect(
        testhelper.champRechercheSélecteurMultiple(i18n.ÉLÈVE.FORMATIONS.FORMATIONS_ENVISAGÉES.LABEL),
      ).toHaveText("");
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
    });
  });

  test.describe("En étant à l'étape suivante", () => {
    test("En faisait précédent avec mon navigateur je retrouve bien les informations renseignées", async ({ page }) => {
      // GIVEN
      const testhelper = new Test(page);
      await testhelper.naviguerVersLaPage();
      const situationFormations = i18n.ÉLÈVE.FORMATIONS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL;

      // WHEN
      await testhelper.renseignerChampSituationFormations(situationFormations);
      await testhelper.renseignerChampRechercheFormations(testhelper.FORMATION_RECHERCHÉE);
      await testhelper.boutonFavoriSuggéré(testhelper.PREMIÈRE_FORMATION).click();
      await testhelper.boutonFavoriSuggéré(testhelper.SECONDE_FORMATION).click();
      await testhelper.soumettreLeFormulaire();
      await page.goBack();

      // THEN
      await expect(testhelper.champSituationFormations(situationFormations)).toBeChecked();
      await expect(testhelper.listeDesFavorisSélectionnés().getByRole("listitem")).toHaveCount(2);
      await expect(testhelper.boutonFavoriSélectionné(testhelper.PREMIÈRE_FORMATION)).toBeVisible();
      await expect(testhelper.boutonFavoriSélectionné(testhelper.SECONDE_FORMATION)).toBeVisible();
    });
  });
});
