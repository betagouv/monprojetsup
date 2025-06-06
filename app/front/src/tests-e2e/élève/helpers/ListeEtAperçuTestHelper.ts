import { i18n } from "@/configuration/i18n/i18n";
import { formationInMemoryRepository } from "@/features/formation/infrastructure/gateway/formationInMemoryRepository/formationInMemoryRepository";
import { métierInMemoryRepository } from "@/features/métier/infrastructure/gateway/métierInMemoryRepository/métierInMemoryRepository";
import { ÉlèveTestHelper } from "@/tests-e2e/élève/helpers/ÉlèveTestHelper";
import { type Page } from "@playwright/test";

export class ListeEtAperçuTestHelper extends ÉlèveTestHelper {
  public NOMBRE_FORMATIONS_FAVORITES = 4;

  public NOMBRE_MÉTIERS_FAVORIS = 3;

  public constructor(protected _page: Page) {
    super(_page);
  }

  public static readonly récupérerFormationsSuggérées = async () => {
    const formationInMemory = new formationInMemoryRepository();
    const formationsSuggéres = await formationInMemory.suggérer();

    if (formationsSuggéres instanceof Error) return [];

    return formationsSuggéres;
  };

  public naviguerVersLaPageFormations = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto(this.PAGE_FORMATIONS);
  };

  public naviguerVersLaPageFormationsAvecDesFavoris = async () => {
    await this._ajouterFavorisÉlève();
    await this._page.goto(this.PAGE_FORMATIONS);
  };

  public naviguerVersLaPageFavoris = async () => {
    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé();
    await this._page.goto(this.PAGE_FAVORIS);
  };

  public naviguerVersLaPageFavorisAvecDesFavoris = async () => {
    await this._ajouterFavorisÉlève();
    await this._page.goto(this.PAGE_FAVORIS);
  };

  private _ajouterFavorisÉlève = async () => {
    const formations = await this.récupérerToutesFormations();
    const métiers = await this.récupérerTousMétiers();

    await this.seConnecterCommeÉlèveAvecParcoursInscriptionTerminé({
      formations: formations.slice(0, this.NOMBRE_FORMATIONS_FAVORITES).map((it) => it.id),
      métiersFavoris: métiers.slice(0, this.NOMBRE_MÉTIERS_FAVORIS).map((it) => it.id),
    });
  };

  public appuyerSurLeBoutonAjouterEnFavori = async () => {
    return this.boutonAjouterEnFavori().click();
  };

  public appuyerSurLeBoutonSupprimerFavori = async () => {
    return this._boutonSupprimerFavori().click();
  };

  public boutonAjouterEnFavori = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.AJOUTER_À_MA_SÉLECTION });
  };

  private _boutonSupprimerFavori = () => {
    return this._page.getByRole("button", { name: i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION });
  };

  public titrePage = async () => {
    return this._page.getByRole("heading", { level: 1 }).textContent();
  };

  public listeFormations = () => {
    return this._page.getByRole("list", { name: i18n.ACCESSIBILITÉ.LISTE_FORMATIONS });
  };

  public listeMétiers = () => {
    return this._page.getByRole("list", { name: i18n.ACCESSIBILITÉ.LISTE_MÉTIERS });
  };

  public cliquerSurLaDernièreCarteFormation = async () => {
    return this._dernièreCarteFormation().click();
  };

  public contenuDeLaPremièreCarteFormation = async () => {
    return this._premièreCarteFormation().textContent();
  };

  public contenuDeLaDernièreCarteFormation = async () => {
    return this._dernièreCarteFormation().textContent();
  };

  public cartesFormations = () => {
    return this.listeFormations().locator(":scope > li");
  };

  private readonly _premièreCarteFormation = () => {
    return this.listeFormations().getByRole("listitem").first();
  };

  private readonly _dernièreCarteFormation = () => {
    return this.listeFormations().getByRole("listitem").last();
  };

  public cliquerSurLaDernièreCarteMétier = async () => {
    return this._dernièreCarteMétier().click();
  };

  public contenuDeLaPremièreCarteMétier = async () => {
    return this._premièreCarteMétier().textContent();
  };

  public contenuDeLaDernièreCarteMétier = async () => {
    return this._dernièreCarteMétier().textContent();
  };

  public cartesMétiers = () => {
    return this.listeMétiers().locator(":scope > li");
  };

  private readonly _premièreCarteMétier = () => {
    return this.listeMétiers().getByRole("listitem").first();
  };

  private readonly _dernièreCarteMétier = () => {
    return this.listeMétiers().getByRole("listitem").last();
  };

  public récupérerToutesFormations = async () => {
    const formationInMemory = new formationInMemoryRepository();
    const formations = await formationInMemory.récupérerToutes();

    if (formations instanceof Error) return [];

    return formations;
  };

  public récupérerTousMétiers = async () => {
    const métierInMemory = new métierInMemoryRepository();
    const métiers = await métierInMemory.récupérerTous();

    if (métiers instanceof Error) return [];

    return métiers;
  };
}
